package hardware;
import java.io.IOException;

import lejos.nxt.BasicMotor;
import lejos.nxt.NXTMotor;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteMotorPort;

public class Feeder implements AutoCloseable {
	public RemoteMotor feeder;
	public RemoteMotor belt;
	
	private NXTCommand conn;
	
	public Feeder(NXTCommand conn) throws InterruptedException {
		feeder = new RemoteMotor(conn, 0);  // port A
		belt = new RemoteMotor(conn, 1);  // port B
		this.conn = conn;

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
	}
	
	public void feed() throws InterruptedException {
		feeder.setSpeed(720);
		feeder.rotateTo(-180);
		feeder.setSpeed(180);
		feeder.rotateTo(0);
		feeder.flt();
	}
	
	@Override
	public void close() throws IOException {
		feeder.flt();
		belt.flt();
		conn.disconnect();
	}
}
