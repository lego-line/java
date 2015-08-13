package hardware;
import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;

public class Junction {
	public RemoteMotor sideBelt;
	public RemoteMotor mainBelt;
	public RemoteMotor pusher;
	
	public Junction(NXTCommand conn) {
		pusher   = new RemoteMotor(conn, 0);  // port A
		sideBelt = new RemoteMotor(conn, 1);  // port C
		mainBelt = new RemoteMotor(conn, 2);  // port C
	}
}