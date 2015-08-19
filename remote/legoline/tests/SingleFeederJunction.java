package legoline.tests;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import legoline.hardware.*;

public class SingleFeederJunction {
	
	static Callable<Void> advancer(final Belt b, final float amt) {
		return new Callable<Void>() {public Void call() throws Exception {
			b.advance(amt);
			return null;
		}};
	}

	public static void main(String[] args) throws Exception {
		try(LegoLine line = LegoLine.createFromConnectedNXTs()) {
			line.initialize();
			
			final Feeder f = line.feeders.get(0);
			final Junction j = line.junctions.get(0);
			
			f.feed();
			f.belt.advance(8);
			f.feed();
			f.belt.advance(8);
			f.feed();
			f.belt.advance(2);
			
			// advance two belts in parallel
			Executors.newCachedThreadPool().invokeAll(Arrays.asList(
				advancer(f.belt, 6),
				advancer(j.sideBelt, 8)
			));
			
			// advance two belts in parallel
			Executors.newCachedThreadPool().invokeAll(Arrays.asList(
				advancer(f.belt, 8),
				advancer(j.sideBelt, 10)
			));
			
		}
	}

}
