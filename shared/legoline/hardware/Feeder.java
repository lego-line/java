package legoline.hardware;
import java.io.IOException;

import legoline.Pallet;
import lejos.nxt.ADSensorPort;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteMotorPort;
import lejos.nxt.remote.RemoteSensorPort;
import lejos.robotics.EncoderMotor;
import lejos.robotics.RegulatedMotor;

public class Feeder implements AutoCloseable {
	public RegulatedMotor feeder;
	public EncoderMotor feederRaw;
	public Belt belt;
	
	public ObjectDetector inSensor;
	public ObjectDetector outSensor;
	
	private NXTCommand conn;
	private boolean isZeroed = false;
	
	
	public Feeder(NXTCommand conn) {
		RegulatedMotor beltMotor;
		ADSensorPort inPort;
		ADSensorPort outPort;
		
		// switch between remote and local cases
		if(conn == null) {
			feeder = Motor.A;
			feederRaw = new NXTMotor(MotorPort.A);
			beltMotor = Motor.B;
			inPort = SensorPort.S1;
			outPort = SensorPort.S2;
		}
		else {
			feeder = new RemoteMotor(conn, 0);
			feederRaw = new NXTMotor(new RemoteMotorPort(conn, 0));
			beltMotor = new RemoteMotor(conn, 1);
			inPort = new RemoteSensorPort(conn, 0);
			outPort = new RemoteSensorPort(conn, 1);
		}
		
		belt = new Belt(beltMotor, 18);
		inSensor = new ObjectDetector(
			new LightSensor(inPort),
			new ObjectDetector.ReadingPair(430, 270),
			new ObjectDetector.ReadingPair(425, 240)
		);
		outSensor =  new ObjectDetector(
			new LightSensor(outPort),
			new ObjectDetector.ReadingPair(410, 400),
			new ObjectDetector.ReadingPair(480, 290)
		);
				
		this.conn = conn;
		

		if(conn == null && feeder instanceof RemoteMotor)
			System.err.println(
				"Warning: local constructor called in remote environment" +
				"- using default remote NXT"
			);
	}
	
	public void reset() throws InterruptedException {
		// first, get an unregulated copy of pusher, and zero it
		feederRaw.setPower(25);
		feederRaw.backward();
		Util.waitForStall(feederRaw);
		feederRaw.stop();
		
		// now unbend the axle
		feeder.setSpeed(180);
		feeder.rotate(30);
		feeder.resetTachoCount();
		feeder.flt();

		isZeroed = true;
	}
	
	public void feed() throws InterruptedException {
		if(!isZeroed) throw new IllegalStateException("Must reset first");

		feeder.setSpeed(720);
		feeder.rotateTo(180);
		
		feeder.setSpeed(180);
		feeder.rotateTo(0);
		feeder.flt();
		
		belt.palletAdded(new Pallet());
	}
	
	@Override
	public void close() throws IOException {
		feeder.flt();
		belt.close();
		if(conn != null) conn.disconnect();
	}
}
