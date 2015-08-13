package hardware;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lejos.robotics.Encoder;

public class Util {	
	static void waitForStall(Encoder m) throws InterruptedException {
		int lastTachoCount = m.getTachoCount();
		while(true) {
			Thread.sleep(250);
			int nextTachoCount = m.getTachoCount();
			if(Math.abs(lastTachoCount - nextTachoCount) < 3)
				break;
			lastTachoCount = nextTachoCount;
		}
	}
}
