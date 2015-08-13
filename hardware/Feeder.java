package hardware;
import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;

public class Feeder {
	public RemoteMotor feeder;
	public RemoteMotor belt;
	
	private NXTCommand conn;
	
	public Feeder(NXTCommand conn) throws InterruptedException {
		feeder = new RemoteMotor(conn, 0);  // port A
		belt = new RemoteMotor(conn, 1);  // port B
		this.conn = conn;
		
		// reset the feeder
		feeder.setPower(25);
		feeder.rotate(180, false);
		Thread.sleep(500);
		feeder.stop();
		feeder.rotate(-50);  // this is how much the axle bends by!
		feeder.resetTachoCount();
	}
	
	public void feed() throws InterruptedException {
		feeder.setSpeed(720);
		feeder.rotateTo(-180);
		feeder.setSpeed(180);
		feeder.rotateTo(0);
		feeder.flt();
		System.out.println("Fed");
	}
	
	public void close() throws IOException {
		feeder.flt();
		conn.disconnect();
	}
}
