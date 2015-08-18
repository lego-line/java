package legoline.tests;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

import legoline.Pallet;
import legoline.hardware.*;

public class StandaloneFeeder {
	public static void main(String[] args) throws Exception {
		try(LegoLine line = LegoLine.createFromConnectedNXTs()) {
			line.initialize();
			
			final Feeder f = line.feeders.get(0);
			
			final float speed = 5; // studs per second
			final float palletSpacing = 8; // studs
			final int period = (int) (1000 * palletSpacing / speed); // ms
			
			float feedDelay = 0;
			
			while(true) {
				f.belt.setSpeed(speed);
				f.belt.enable();
				
				// wait for the previous run to clear
				while(f.belt.leadingSpace() + feedDelay * speed < palletSpacing)
					Thread.yield();
				
				// spit out pallets at time intervals
				long last = Long.MIN_VALUE;
				while(!f.outSensor.hasObject()) {
					long now = System.currentTimeMillis();
					if(now > last + period) {
						f.feed();
						feedDelay = (System.currentTimeMillis() - now) / 1000f;
						last = now;
					}
					Thread.yield();
				}
				f.belt.disable();
				
				// wait for the end to clear
				while(f.outSensor.hasObject())
					Thread.yield();
			}
		}
	}

}
