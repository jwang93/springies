package simulation;

import java.awt.Dimension;

public class Muscle extends Spring {

	// number of frames that comprise a full period in the sin cycle
	private static final int FRAMES_PER_CYCLE = 75;
	private static final double TWO_TIMES_PI = 2 * Math.PI;
	private static final double SIN_INCREMENT_PER_UPDATE = TWO_TIMES_PI / FRAMES_PER_CYCLE;
	
	private double myAmplitude;
	private double myStartLength;
	private double myCurrentSinIncrement;
	
	public Muscle(Mass start, Mass end, double length, double kVal, double amplitude) {
		super(start, end, length, kVal);
		myStartLength = length;
		myAmplitude = amplitude;
		myCurrentSinIncrement = 0.0;
	}
	
	public void update(double elapsedTime, Dimension bounds) {
		updateRestLength();
		super.update(elapsedTime, bounds);
	}
	
	public void updateRestLength() {
		double length;
		myCurrentSinIncrement += SIN_INCREMENT_PER_UPDATE;
		if (myCurrentSinIncrement > TWO_TIMES_PI) {
			myCurrentSinIncrement -= TWO_TIMES_PI;
		}
		length = Math.sin(myCurrentSinIncrement) * myAmplitude;
		length += myStartLength;
		super.setLength(length);
	}

}
