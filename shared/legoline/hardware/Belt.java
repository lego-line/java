package legoline.hardware;
import java.util.*;
import legoline.Pallet;
import lejos.robotics.RegulatedMotor;

/**
 * Represents a powered conveyor belt section, and exposes linear motion
 * commands which map into motor commands
 * 
 * Also keeps track of which pallets are currently on the line, if told when
 * they are added to the belt
 */
public class Belt implements AutoCloseable {
	public final float length;
	private final RegulatedMotor motor;
	
	// mechanical configuration
	private final float gearRatio = 1f/3;
	private static final float SPROCKET_TEETH = 6;
	private static final float CHAIN_LENGTH = 1.5f;
	
	
	Belt(RegulatedMotor motor, float length) {
		this.length = length;
		this.motor = motor;
	}
	
	/**
	 * @return the number of degrees of motor axle rotation required to
	 *         advance the conveyor by one lego unit (stud)
	 */
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
	/**
	 * @return the total distance in studs travelled by the conveyor since startup
	 */
	public float getDistanceTravelled() {
		return motor.getTachoCount() / degreesPerStud();
	}
	
	/**
	 * @return the _target_ speed of the conveyer, in studs per second.
	 */
	public float getSpeed() {
		return motor.getSpeed() / degreesPerStud();
	}
	
	/**
	 * @return the maximum possible speed, given the battery voltage
	 */
	public float getMaxSpeed() {
		return motor.getMaxSpeed() / degreesPerStud();
	}
	
	/**
	 * @return the maximum possible speed for an ideal battery
	 */
	public float getFullyChargedMaxSpeed() {
		return 900 / degreesPerStud();
	}
	
	// Functions to keep track of live pallets
	private final Map<Pallet, Float> beltOffsetByPallet = new HashMap<>();
	
	/**
	 * Discard any pallet objects which have left the belt (ie, are at a position > length)
	 * @param offset
	 */
	private void updatePacketList(float offset) {
		Iterator<Map.Entry<Pallet, Float>> iter = beltOffsetByPallet.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Pallet, Float> e = iter.next();
			if(offset - e.getValue() > length)
				iter.remove();
		}
	}
	
	/**
	 * Record a pallet as having been placed on the belt at position 0
	 */
	public void palletAdded(Pallet p) {
		float offset = getDistanceTravelled();
		if(beltOffsetByPallet.containsKey(p)) throw new IllegalStateException("Pallet already on the conveyor");
		beltOffsetByPallet.put(p, offset);

		updatePacketList(offset);
	}
	
	/**
	 * @return a map of pallet objects to their current position on the belt
	 */
	public Map<Pallet, Float> getActivePallets() {
		float pos = getDistanceTravelled();
		updatePacketList(getDistanceTravelled());
		
		Map<Pallet, Float> worldPositions = new HashMap<>();
		for (Map.Entry<Pallet, Float> e : beltOffsetByPallet.entrySet()) {
			worldPositions.put(e.getKey(), pos - e.getValue());
		}
		
		return worldPositions;
	}
	
	/**
	 * @return the amount of space free at the start of the belt
	 */
	public float leadingSpace() {
		try {
			return getDistanceTravelled() - Collections.max(beltOffsetByPallet.values());
		}
		catch (NoSuchElementException e) {
			return Float.POSITIVE_INFINITY;
		}
	}

	@Override
	public void close() {
		disable();		
	}
}
