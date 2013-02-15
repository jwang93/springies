package simulation;

import java.awt.Dimension;

/**
 * Extension of Spring class that allows for variable rest length.
 * @author james
 *
 */
public class Muscle extends Spring {

    // number of frames that comprise a full period in the sin cycle
    private static final int FRAMES_PER_CYCLE = 75;
    private static final double TWO_TIMES_PI = 2 * Math.PI;
    private static final double SIN_INCREMENT_PER_UPDATE = TWO_TIMES_PI / FRAMES_PER_CYCLE;

    private double myAmplitude;
    private double myStartLength;
    private double myCurrentSinIncrement;

    /**
     * Constructor for the Muscle class
     * @param start is the first mass endpoint
     * @param end is the second mass endpoint
     * @param length is the rest length
     * @param kVal is the springiness constant
     * @param amplitude is the degree of variation in rest length over time
     */
    public Muscle (Mass start, Mass end, double length, double kVal, double amplitude) {
        super(start, end, length, kVal);
        myStartLength = length;
        myAmplitude = amplitude;
        myCurrentSinIncrement = 0.0;
    }

    @Override
    public void update (double elapsedTime, Dimension bounds) {
        updateRestLength();
        super.update(elapsedTime, bounds);
    }

    /**
     * Changes the rest length of the muscle according to the sin cycle
     */
    public void updateRestLength () {
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
