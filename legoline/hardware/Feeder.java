package legoline.hardware;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import legoline.Pallet;
import lejos.nxt.LightSensor;
import lejos.nxt.NXTMotor;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteMotorPort;
import lejos.nxt.remote.RemoteSensorPort;
import lejos.robotics.LampLightDetector;
import lejos.robotics.LightDetector;

public class Feeder implements AutoCloseable {
	public RemoteMotor feeder;
	public Belt belt;
	
	public ObjectDetector inSensor;
	public ObjectDetector outSensor;
	
	private NXTCommand conn;
	private boolean isZeroed = false;
	
	public Feeder(NXTCommand conn) {
		feeder = new RemoteMotor(conn, 0);  // port A
		belt = new Belt(new RemoteMotor(conn, 1), 18); // port B
		
		inSensor = new ObjectDetector(
			new LightSensor(new RemoteSensorPort(conn, 0)),   // port 1
			new ObjectDetector.ReadingPair(430, 270),
			new ObjectDetector.ReadingPair(425, 240)
		);
		outSensor =  new ObjectDetector(
			new LightSensor(new RemoteSensorPort(conn, 1)),  // port 2
			new ObjectDetector.ReadingPair(410, 400),
			new ObjectDetector.ReadingPair(480, 290)
		);
				
		this.conn = conn;
	}
	
	public void reset() throws InterruptedException {
		// first, get an unregulated copy of pusher, and zero it
		NXTMotor temp = new NXTMotor(new RemoteMotorPort(conn, 0));
		temp.setPower(25);
		temp.forward();
		Util.waitForStall(temp);
		temp.stop();
		
		// now unbend the axle
		feeder.setSpeed(180);
		feeder.rotate(-30);
		feeder.resetTachoCount();
		feeder.flt();

		isZeroed = true;
	}
	
	public void feed() throws InterruptedException {
		if(!isZeroed) throw new IllegalStateException("Must reset first");

		feeder.setSpeed(720);
		feeder.rotateTo(-180);
		
		feeder.setSpeed(180);
		feeder.rotateTo(0);
		feeder.flt();
		
		belt.palletAdded(new Pallet());
	}
	
	@Override
	public void close() throws IOException {
		feeder.flt();
		belt.close();
		conn.disconnect();
	}
}
