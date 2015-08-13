package legoline.hardware;
import lejos.robotics.RegulatedMotor;

public class Belt implements AutoCloseable {
	public final float length;
	private final RegulatedMotor motor;
	
	float gearRatio = 1f/3;
	
	static final float SPROCKET_TEETH = 6;
	static final float CHAIN_LENGTH = 1.5f;

	Belt(RegulatedMotor motor) {
		this(motor, 0);
	}
	Belt(RegulatedMotor motor, float length) {
		this.length = length;
		this.motor = motor;
	}
	
	private float degreesPerStud() {
		return 360f / (CHAIN_LENGTH * SPROCKET_TEETH) / gearRatio;
	}
	
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

	@Override
	public void close() {
		disable();		
	}
}