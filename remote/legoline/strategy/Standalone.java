package legoline.strategy;

import legoline.Pallet;
import legoline.hardware.*;

public class Standalone extends Strategy {
	@Override
	public void run(Feeder f) throws Exception {
		final float speed = 3; // studs per second
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
	
	@Override
	public void run(Junction j) throws Exception {
		final float speed = 6; // studs per second
		final float palletSpacing = 16; // studs
		final int period = (int) (1000 * palletSpacing / speed); // ms
		
		float feedDelay = 0;
		
		j.sideBelt.setSpeed(speed);
		j.sideBelt.enable();
		
		boolean new_pallet = false;
		
		while(true) {
			if(j.sideInSensor.hasObject()) {
				j.sideBelt.enable();
				new_pallet = true;
			}
			else if(new_pallet) {
				j.sideBelt.palletAdded(new Pallet());
				new_pallet = false;
				System.out.println("Added pallet");
			}
			
			float space = j.sideBelt.trailingSpace();
			System.out.println("Space: " + space);
			if(space < -3) {
				j.sideBelt.disable();
				j.transfer();
				j.sideBelt.enable();
				j.sideBelt.popPallets();
				j.mainBelt.palletAdded(new Pallet());
				j.mainBelt.enable();
			}
			
			if(j.outSensor.hasObject()) j.mainBelt.popPallets();
			
			if(!new_pallet && space == Float.POSITIVE_INFINITY) j.sideBelt.disable();
			if(j.mainBelt.trailingSpace() == Float.POSITIVE_INFINITY) j.mainBelt.disable();
		}
	}
	public static void main(String[] args) throws Exception {
		try(LegoLine line = LegoLine.createFromConnectedNXTs()) {
			line.initialize();
						
			new Standalone().run(line);	
		}
	}
}
