package legoline.hardware;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import legoline.Pallet;
import lejos.robotics.RegulatedMotor;

public class Belt implements AutoCloseable {
	public final float length;
	private final RegulatedMotor motor;
	
	
	// mechanical configuration
	private final float gearRatio = 1f/3;
	private static final float SPROCKET_TEETH = 6;
	private static final float CHAIN_LENGTH = 1.5f;
	
	private final Map<Pallet, Float> palletPositions = new HashMap<>();
	
	Belt(RegulatedMotor motor, float length) {
		this.length = length;
		this.motor = motor;
	}
	
	private float degreesPerStud() {
		return 360f / (CHAIN_LENGTH * SPROCKET_TEETH) / gearRatio;
	}
	
	// expose some limited motor operations
	public void setSpeed(float studsPerSecond) {
		motor.setSpeed((int)(degreesPerStud() * studsPerSecond));
	}
	public void advance(float studs) {
		motor.rotate((int)(degreesPerStud() * studs));
		motor.flt();
	}
	public void enable() {
		motor.forward();
	}
	public void disable() {
		motor.flt();
	}
	public boolean isMoving() {
		return motor.isMoving();
	}
	
	// functions returning information about speed and position
	public float getPosition() {
		return motor.getTachoCount() / degreesPerStud();
	}
	public float getSpeed() {
		return motor.getSpeed() / degreesPerStud();
	}
	public float getMaxSpeed() {
		return motor.getMaxSpeed() / degreesPerStud();
	}
	public float getFullyChargedMaxSpeed() {
		return 900 / degreesPerStud();
	}
	
	// Functions to keep track of live pallets
	public void palletAdded(Pallet p) {
		float start = getPosition();
		if(palletPositions.containsKey(p)) throw new IllegalStateException("Pallet already on the conveyor");
		palletPositions.put(p, start);

		updatePacketList(start);
	}
	private void updatePacketList(float position) {
		Iterator<Map.Entry<Pallet, Float>> iter = palletPositions.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Pallet, Float> e = iter.next();
			if(position - e.getValue() > length)
				iter.remove();
		}
	}
	
	public Map<Pallet, Float> getActivePallets() {
		float pos = getPosition();
		updatePacketList(getPosition());
		
		Map<Pallet, Float> worldPositions = new HashMap<>();
		for (Map.Entry<Pallet, Float> e : palletPositions.entrySet()) {
			worldPositions.put(e.getKey(), pos - e.getValue());
		}
		
		return worldPositions;
	}
	
	public float leadingSpace() {
		try {
			return getPosition() - Collections.max(palletPositions.values());
		}
		catch (NoSuchElementException e) {
			return Float.POSITIVE_INFINITY;
		}
	}

	// cleanup code
	@Override
	public void close() {
		disable();		
	}
}
