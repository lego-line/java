import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import hardware.*;
import ui.*;
import lejos.nxt.remote.*;
import lejos.pc.comm.*;

public class Main extends JFrame {
	private static final long serialVersionUID = -5025003895547053468L;

	public static void main(String[] args) throws InterruptedException {
        new Main().setVisible(true);
    }
    
    public Main() throws InterruptedException {
    	findBricks();
		initComponents();
    }
    
	final List<Feeder> feeds = new ArrayList<>();
	final List<Junction> juncs = new ArrayList<>();
    
	public void findBricks() throws InterruptedException {	
		final List<NXTComm> toClose = new ArrayList<>();
		
		final List<NXTInfo> connected = new ArrayList<>();
		final List<NXTInfo> busy = new ArrayList<>();
		
		for (NXTInfo nxtInfo : new NXTConnectionManager().search()) {
			if (nxtInfo.name.equals("Unknown"))
				busy.add(nxtInfo);
			else
				connected.add(nxtInfo);
		}
		
		if (busy.size() != 0) {
			System.out.println("Found " + busy.size() + " bricks that are in use elsewhere");
		}
		
		if (connected.size() == 0) {
			System.err.println("No bricks found");
			return;
		}
		
		for (NXTInfo nxtInfo : connected) {			
			NXTComm comm;
			try {
				comm = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
				comm.open(nxtInfo);
			} catch (NXTCommException e) {
				continue;
			}
			NXTCommand command = new NXTCommand(comm);
			
			if (nxtInfo.name.startsWith("Feeder")) {
				feeds.add(new Feeder(command));
				toClose.add(comm);
			}
			else if (nxtInfo.name.startsWith("Merge")) {
				juncs.add(new Junction(command));
				toClose.add(comm);
			}
			else {
				try {
					System.out.println("Renaming brick");
					command.setFriendlyName("Merge");
					command.disconnect();
					comm.close();
				} catch (IOException e) { }
			}
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		    	for (Feeder feed : feeds) {
					try {
						feed.close();
					} catch (IOException e) { }
		    	}
		    	
		    	for (Junction junc : juncs) {
					try {
						junc.close();
					} catch (IOException e) { }
		    	}
		   
		        for (NXTComm nxtComm : toClose) {
					try {
						nxtComm.close();
					} catch (IOException e) { }
				}
		    }
		});
	}
	
	private void initComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Could not load native theme");
		}

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Legoline control");
		
		final Container content = getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		for (Feeder feeder : feeds)
			content.add(new FeederPanel(feeder));
		for (Junction junction : juncs)
			content.add(new JunctionPanel(junction));
		pack();
	}
}