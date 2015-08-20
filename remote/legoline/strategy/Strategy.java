package legoline.strategy;
import java.util.ArrayList;
import java.util.List;

import legoline.hardware.*;

public abstract class Strategy {
	public void run(Feeder f) throws Exception {};
	public void run(Junction j) throws Exception {};
	
	public void run(LegoLine line) {
		// build a thread for each device, to run them separately
		List<Thread> threads = new ArrayList<>();
		
		for (final Feeder f : line.feeders) {
			threads.add(new Thread(new Runnable() {		
				public void run() {
					try {
						Strategy.this.run(f);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}));
		}
		for (final Junction j : line.junctions) {
			threads.add(new Thread(new Runnable() {		
				public void run() {
					try {
						Strategy.this.run(j);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}));
		}
		
		// start all the threads, and wait for them to all complete
		for (Thread thread : threads) {
			thread.start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
