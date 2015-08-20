package legoline.hardware;

import lejos.robotics.LampLightDetector;

public class ObjectDetector {
	public static class ReadingPair {
		public final int lit;
		public final int unlit;
	
		public ReadingPair(int lit, int unlit) {
			this.lit = lit;
			this.unlit = unlit;
		}
		
		
		public double dist(ReadingPair other) {
			int dLit = lit - other.lit;
			int dUnlit = unlit - other.unlit;
			return Math.sqrt(dLit*dLit + dUnlit*dUnlit);
		}
	}

	final LampLightDetector sensor;
	
	// TODO: allow covariance in distributions
	final ReadingPair presentMean;
	final ReadingPair absentMean;
	
	ReadingPair lastReading;
	
	public ObjectDetector(LampLightDetector sensor, ReadingPair absent, ReadingPair present) {
		this.sensor = sensor;
		this.presentMean = present;
		this.absentMean = absent;
	}
	
	public boolean hasObject() throws InterruptedException {
		lastReading = read();
		return lastReading.dist(presentMean) < lastReading.dist(absentMean);
	}
	public ReadingPair getLastReading() {
		return lastReading;
	}
	
	private ReadingPair read() throws InterruptedException {
		sensor.setFloodlight(true);
		Thread.sleep(10);
		int lit = sensor.getNormalizedLightValue();

		sensor.setFloodlight(false);
		Thread.sleep(10);
		int unlit = sensor.getNormalizedLightValue();
		
		return new ReadingPair(lit, unlit);
	}
	
	public static ObjectDetector upwardsFacing(LampLightDetector sensor) {
		return new ObjectDetector(
			sensor,
			new ObjectDetector.ReadingPair(410, 400),
			new ObjectDetector.ReadingPair(480, 290)
		);
							
	}
	
}
