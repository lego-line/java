package legoline.hardware;
import java.io.IOException;

import lejos.nxt.*;
import lejos.nxt.remote.*;
import lejos.robotics.*;

public class Junction implements AutoCloseable {
	public Belt sideBelt;
	public Belt mainBelt;
	
	public RegulatedMotor pusher;
	public EncoderMotor pusherRaw;

	public ObjectDetector sideInSensor;
	public ObjectDetector mainInSensor;
	public ObjectDetector outSensor;
	
	private NXTCommand conn;
	private boolean isZeroed = false;

	
	/**
	 * Connect to a given junction object
	 * 
	 * @param conn  the connection to the nxt. If null, assume running on the brick
	 */
	public Junction(NXTCommand conn) {
		RegulatedMotor sideBeltMotor;
		RegulatedMotor mainBeltMotor;
		ADSensorPort sideInPort;
		ADSensorPort mainInPort;
		ADSensorPort outPort;
	
		// switch between remote and local cases
		if(conn == null) {
			sideBeltMotor = Motor.A;
			pusher = Motor.B;
			pusherRaw = new NXTMotor(MotorPort.B);
			mainBeltMotor = Motor.C;
			
			sideInPort = SensorPort.S1;
			mainInPort = SensorPort.S2;
			outPort = SensorPort.S3;
		}
		else {
			sideBeltMotor = new RemoteMotor(conn, 0);
			pusher = new RemoteMotor(conn, 1);
			pusherRaw =  new NXTMotor(new RemoteMotorPort(conn, 1));
			mainBeltMotor = new RemoteMotor(conn, 2);
			
			sideInPort = new RemoteSensorPort(conn, 0);
			mainInPort = new RemoteSensorPort(conn, 1);
			outPort = new RemoteSensorPort(conn, 2);
		}
		
		sideBelt = new Belt(MirrorMotor.invertMotor(sideBeltMotor), 21);  // port C
		mainBelt = new Belt(MirrorMotor.invertMotor(mainBeltMotor), 27);  // port C

		sideInSensor = ObjectDetector.upwardsFacing(new LightSensor(sideInPort));
		mainInSensor = ObjectDetector.upwardsFacing(new LightSensor(mainInPort));
		outSensor = ObjectDetector.upwardsFacing(new LightSensor(outPort));
		
		this.conn = conn;

		if(conn == null && pusher instanceof RemoteMotor)
			System.err.println(
				"Warning: local constructor called in remote environment" +
				"- using default remote NXT"
			);
	}
	

	public void reset() throws InterruptedException {
		// first, get an unregulated copy of pusher, and zero it
		pusherRaw.setPower(25);
		pusherRaw.forward();
		Util.waitForStall(pusherRaw);
		pusherRaw.stop();

		// now unbend the axle!
		pusher.setSpeed(180);
		pusher.rotate(-25);
		pusher.resetTachoCount();
		pusher.flt();
		
		isZeroed = true;
	}
	
	public void transfer() throws InterruptedException {
		if(!isZeroed) throw new IllegalStateException("Must reset first");

		pusher.setSpeed(180);
		pusher.rotateTo(-140);
		if(!mainBelt.isMoving()) {
			mainBelt.advance(1.0f);
		}
		pusher.rotateTo(0);
		pusher.flt();
	}
	
	@Override
	public void close() throws IOException {
		pusher.flt();
		sideBelt.close();
		mainBelt.close();
		if(conn != null)
			conn.disconnect();
	}

}