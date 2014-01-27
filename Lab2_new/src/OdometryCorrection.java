import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private static int lightVal = 0;
	private static final int LIGHT_THRESHOLD = 390;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		ColorSensor sensor = new ColorSensor(SensorPort.S1);
		sensor.setFloodlight(true);
		sensor.setFloodlight(ColorSensor.Color.GREEN);

		while (true) {
			correctionStart = System.currentTimeMillis();

			// put your correction code here
			lightVal = sensor.getNormalizedLightValue();
			if (lightVal < LIGHT_THRESHOLD){
				double theta = odometer.getTheta();
				if (theta > 315 || theta < 45 || (135 < theta && theta < 225) ){ //affects y
					double y = odometer.getY();
					y = nearest30(y);
					odometer.setY(y);
				}
				else{ //affects x
					double x = odometer.getX();
					x = nearest30(x);
					odometer.setX(x);
				}
			}
			
			
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	public static double nearest30(double value){
		int w;
		if ((value % 1) > 0.5 ){
			w = (int) value;
		}
		else{
			w = (int) value + 1;
		}
		int q = w - 15;
		int remainder = q % 30;
		int dividend = q / 30;
		int a = 0;
		if (remainder > 15){
			a = 30;
		}
		return (double) (dividend * 30 + a + 15);
	}

	public static int getLightVal() {
		return lightVal;
	}
}