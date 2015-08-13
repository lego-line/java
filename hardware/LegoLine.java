package hardware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnectionManager;
import lejos.pc.comm.NXTInfo;

public class LegoLine implements AutoCloseable {
	public List<Feeder> feeders = new ArrayList<>();
	public List<Junction> junctions = new ArrayList<>();
	
	public void initialize() throws InterruptedException {
		// queue up all the resets
		List<Callable<Void>> tasks = new ArrayList<>();
		for (final Feeder feeder : feeders)
			tasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					feeder.reset();
					return null;
				}
			});
		for (final Junction junction : junctions)
			tasks.add(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					junction.reset();
					return null;
				}
			});
		
		// and invoke them all asynchronously
		Executors.newCachedThreadPool().invokeAll(tasks);
	}
	
	public static LegoLine createFromConnectedNXTs() throws IOException {
		final List<NXTInfo> busy = new ArrayList<>();
		final List<NXTInfo> feederInfo = new ArrayList<>();
		final List<NXTInfo> junctionInfo = new ArrayList<>();
		final List<NXTInfo> otherInfo = new ArrayList<>();
		
		// Group the attached NXTs by name
		for (NXTInfo nxtInfo : new NXTConnectionManager().search()) {
			if (nxtInfo.name.equals("Unknown"))
				busy.add(nxtInfo);
			else if (nxtInfo.name.startsWith("Feeder"))
				feederInfo.add(nxtInfo);
			else if (nxtInfo.name.startsWith("Merge"))
				junctionInfo.add(nxtInfo);
			else
				otherInfo.add(nxtInfo);
		}
		
		// Check group sizes
		if (busy.size() != 0) {
			System.err.println("Found " + busy.size() + " bricks that are in use elsewhere");
		}
		if (feederInfo.size() + junctionInfo.size() == 0) {
			throw new IOException("No bricks found");
		}
		if(otherInfo.size() != 0) {
			System.out.println("Found some unrecognized NXTs");
		}

		// Connect to the NXTs
		final List<NXTComm> toClose = new ArrayList<>();
		final LegoLine line = new LegoLine();
		for (NXTInfo nxtInfo : feederInfo) {
			NXTComm comm;
			try {
				comm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
				comm.open(nxtInfo);
				toClose.add(comm);
			} catch (NXTCommException e) {
				continue;
			}
			NXTCommand command = new NXTCommand(comm);
			line.feeders.add(new Feeder(command));
		}
		for (NXTInfo nxtInfo : junctionInfo) {
			NXTComm comm;
			try {
				comm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
				comm.open(nxtInfo);
				toClose.add(comm);
			} catch (NXTCommException e) {
				continue;
			}
			NXTCommand command = new NXTCommand(comm);
			line.junctions.add(new Junction(command));
		}
		
		// make sure we disconnect properly when we shut down
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		        
		    	try {
					line.close();
				} catch (Exception e) { }
		    	
		        for (NXTComm nxtComm : toClose) {
					try {
						nxtComm.close();
					} catch (IOException e) { }
				}
		    }
		});
		
		return line;
	}


	@Override
	public void close() throws Exception {
		boolean threw = false;
		
		for (Feeder feed : feeders) {
			try {
				feed.close();
			} catch (IOException e) { threw = true; }
		}
		
		for (Junction junc : junctions) {
			try {
				junc.close();
			} catch (IOException e) { threw = true; }
		}
		
		if (threw) throw new IOException("Unable to shutdown completely");
	}
}
