import lejos.nxt.Motor;

/*
 * Odometer.java
 * P:\git\DPM-Lab-2\Lab2_Odometry2\.classpath
 */

public class Odometer extends Thread {
	// robot position
	private double x, y, theta, dtheta, dis1, dis2, disF, Tacholeft, Tachoright;
	double leftwheelradius = 2.2865;
	double rightwheelradius = 2.2865;
	double width = 16.5295;//15.648;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
		Tacholeft = 0;
		Tachoright = 0;
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here
			dis1 = (leftwheelradius*Motor.A.getTachoCount())*(Math.PI/180) - Tacholeft;
			dis2 = (rightwheelradius*Motor.B.getTachoCount())*(Math.PI/180) - Tachoright;
			
			disF = (dis1+dis2)/2;
			dtheta = (dis1-dis2)/width;
			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				x = x + disF*Math.sin(theta*(Math.PI/180) + dtheta/2);
				y = y + disF*Math.cos(theta*(Math.PI/180) + dtheta/2);
				theta = theta + (dtheta*(180/Math.PI));
				theta = (360 + theta) % 360;
			}
			Tacholeft = Tacholeft + dis1;
			Tachoright = Tachoright + dis2;
			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}

//import lejos.nxt.Motor;
//
///*
// * Odometer.java
// */
//
//public class Odometer extends Thread {
//	// robot position
//	private double x, y, theta; //theta is positive going counter-clockwise
//
//	// odometer update period, in ms
//	private static final long ODOMETER_PERIOD = 10;
//
//	// lock object for mutual exclusion
//	private Object lock;
//	
//	private double lastTachoLeft, lastTachoRight, radius, width;
//
//	// default constructor
//	public Odometer(double radius, double width) {
//		x = 0.0;
//		y = 0.0;
//		theta = 0.0;
//		lock = new Object();
//		Motor.A.resetTachoCount();
//		Motor.B.resetTachoCount();
//		lastTachoLeft = 0;
//		lastTachoRight = 0;
//		this.radius = radius;
//		this.width = width;
//	}
//
//	// run method (required for Thread)
//	public void run() {
//		long updateStart, updateEnd;
//		double deltaX, deltaY, deltaTRad, deltaTDegrees; //change in x, y positions and angle t
//		double deltaC; //change in distance of the center
//		double currentTachoRight, currentTachoLeft; 
//		double deltaL, deltaR; //These are the changes of distance for each wheel
//
//		while (true) {
//			updateStart = System.currentTimeMillis();
//			// put (some of) your odometer code here
//			
//			currentTachoLeft = Motor.A.getTachoCount();
//			currentTachoRight = Motor.B.getTachoCount();
//
//			deltaL = radius * 3.14159 / 180 * (currentTachoLeft - lastTachoLeft);
//			deltaR = radius * 3.14159 / 180 * (currentTachoRight - lastTachoRight);
//			
//			lastTachoLeft = currentTachoLeft;
//			lastTachoRight = currentTachoRight;
//			
//			deltaC = (deltaR + deltaL)/2;
//
//			deltaTRad = (deltaL - deltaR)/width; //TODO: double check this if probs with negatives
//			deltaTDegrees = deltaTRad *180 /3.14159;
//			
//			synchronized (lock) {
//							
//				theta += deltaTDegrees;
//				theta = theta%360;
//
//				deltaX = deltaC * Math.sin(theta);// cos function requires degrees
//				deltaY = deltaC * Math.cos(theta);// as does sin
//				x += deltaX;
//				y += deltaY;
//			}
//
//			// this ensures that the odometer only runs once every period
//			updateEnd = System.currentTimeMillis();
//			if (updateEnd - updateStart < ODOMETER_PERIOD) {
//				try {
//					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
//				} catch (InterruptedException e) {
//					// there is nothing to be done here because it is not
//					// expected that the odometer will be interrupted by
//					// another thread
//				}
//			}
//		}
//	}
//
//	// accessors
//	public void getPosition(double[] position, boolean[] update) {
//		// ensure that the values don't change while the odometer is running
//		synchronized (lock) {
//			if (update[0])
//				position[0] = x;
//			if (update[1])
//				position[1] = y;
//			if (update[2])
//				position[2] = theta;
//		}
//	}
//
//	public double getX() {
//		double result;
//
//		synchronized (lock) {
//			result = x;
//		}
//
//		return result;
//	}
//
//	public double getY() {
//		double result;
//
//		synchronized (lock) {
//			result = y;
//		}
//
//		return result;
//	}
//
//	public double getTheta() {
//		double result;
//
//		synchronized (lock) {
//			result = theta;
//		}
//
//		return result;
//	}
//
//	// mutators
//	public void setPosition(double[] position, boolean[] update) {
//		// ensure that the values don't change while the odometer is running
//		synchronized (lock) {
//			if (update[0])
//				x = position[0];
//			if (update[1])
//				y = position[1];
//			if (update[2])
//				theta = position[2];
//		}
//	}
//
//	public void setX(double x) {
//		synchronized (lock) {
//			this.x = x;
//		}
//	}
//
//	public void setY(double y) {
//		synchronized (lock) {
//			this.y = y;
//		}
//	}
//
//	public void setTheta(double theta) {
//		synchronized (lock) {
//			this.theta = theta;
//		}
//	}
//}