package hardware;
import java.io.IOException;
import lejos.nxt.NXTMotor;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteMotorPort;

public class Junction implements AutoCloseable {
	public RemoteMotor sideBelt;
	public RemoteMotor mainBelt;
	public RemoteMotor pusher;
	
	private NXTCommand conn;
	private boolean isZeroed = false;
	
	public Junction(NXTCommand conn) {
		this.conn = conn;
		
		pusher   = new RemoteMotor(conn, 0);  // port A
		sideBelt = new RemoteMotor(conn, 1);  // port C
		mainBelt = new RemoteMotor(conn, 2);  // port C
	}

	public void reset() throws InterruptedException {
		// first, get an unregulated copy of pusher, and zero it
		NXTMotor temp = new NXTMotor(new RemoteMotorPort(conn, 0));
		temp.setPower(25);
		temp.forward();
		Util.waitForStall(temp);
		temp.stop();
		
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
		pusher.rotateTo(0);
		pusher.flt();
	}
	
	@Override
	public void close() throws IOException {
		pusher.flt();
		sideBelt.flt();
		mainBelt.flt();
		conn.disconnect();
	}

}