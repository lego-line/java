package hardware;
import java.io.IOException;

import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;

public class Junction implements AutoCloseable {
	public RemoteMotor sideBelt;
	public RemoteMotor mainBelt;
	public RemoteMotor pusher;
	
	private NXTCommand conn;
	
	public Junction(NXTCommand conn) {
		this.conn = conn;
		
		pusher   = new RemoteMotor(conn, 0);  // port A
		sideBelt = new RemoteMotor(conn, 1);  // port C
		mainBelt = new RemoteMotor(conn, 2);  // port C
	}
	
	public void close() throws IOException {
		pusher.flt();
		sideBelt.flt();
		mainBelt.flt();
		conn.disconnect();
	}

	public void transfer() throws InterruptedException {
		Thread.sleep(200);
	}
}