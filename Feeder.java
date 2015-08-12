import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;

public class Feeder {
	public RemoteMotor feeder;
	public RemoteMotor belt;
	
	public Feeder(NXTCommand conn) throws InterruptedException {
		feeder = new RemoteMotor(conn, 0);  // port A
		belt = new RemoteMotor(conn, 1);  // port B
		
		// reset the feeder
		feeder.setPower(15);
		feeder.backward();
		Thread.sleep(1000);
		feeder.flt();
		feeder.rotate(25);
		feeder.resetTachoCount();
	}
	
	public void feed() {
		feeder.setPower(50);
		feeder.rotateTo(180);
		feeder.rotateTo(0);
		feeder.flt();
		System.out.println("Fed");
	}
}
