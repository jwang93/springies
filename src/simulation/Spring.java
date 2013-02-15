package simulation;

import java.awt.Color;
import java.awt.Dimension;
import util.Location;
import util.Pixmap;
import util.Sprite;
import util.Vector;


/**
 * XXX.
 * 
 * @author Robert C. Duvall
 */
public class Spring extends Sprite implements SimulationObject {
    // reasonable default values
    public static final Pixmap DEFAULT_IMAGE = new Pixmap("spring.gif");
    public static final int IMAGE_HEIGHT = 20;

    private Mass myStart;
    private Mass myEnd;
    private double myLength;
    private double myK;
    private boolean springUpdated;

    /**
     * XXX.
     */
    public Spring (Mass start, Mass end, double length, double kVal) {
        super(DEFAULT_IMAGE, getCenter(start, end), getSize(start, end));
        myStart = start;
        myEnd = end;
        myLength = length;
        myK = kVal;
        springUpdated = false;
    }

    // /**
    // * Paints the spring--comment out to use .gif files in /images/
    // */
    // @Override
    // public void paint (Graphics2D pen) {
    // pen.setColor(getColor(myStart.distance(myEnd) - myLength));
    // pen.drawLine((int)myStart.getX(), (int)myStart.getY(), (int)myEnd.getX(), (int)myEnd.getY());
    // }

    /**
     * XXX.
     */
    @Override
    public void update (double elapsedTime, Dimension bounds, int LastKeyPressed) {
        if (springUpdated) return;
        springUpdated = true;
        double dx = myStart.getX() - myEnd.getX();
        double dy = myStart.getY() - myEnd.getY();
        // apply hooke's law to each attached mass
        Vector force = new Vector(Vector.angleBetween(dx, dy),
                                  myK * (myLength - Vector.distanceBetween(dx, dy)));
        myStart.applyForce(force);
        force.negate();
        myEnd.applyForce(force);
        // update sprite values based on attached masses
        setCenter(getCenter(myStart, myEnd));
        setSize(getSize(myStart, myEnd));
        setVelocity(Vector.angleBetween(dx, dy), Keywords.ZERO);
    }

    /**
     * Resets the boolean indicating that this Spring has been updated.
     */
    @Override
    public void updateEnd (double elapsedTime, Dimension bounds) {
        springUpdated = false;
    }

    /**
     * Sets the rest length of the spring.
     */
    public void setLength (double length) {
        myLength = length;
    }

    /**
     * Convenience method.
     */
    protected Color getColor (double diff) {
        if (Vector.fuzzyEquals(diff, Keywords.ZERO))
            return Color.BLACK;
        else if (diff < Keywords.ZERO)
            return Color.BLUE;
        else return Color.RED;
    }

    // compute center of this spring
    private static Location getCenter (Mass start, Mass end) {
        return new Location((start.getX() + end.getX()) / 2, (start.getY() + end.getY()) / 2);
    }

    // compute size of this spring
    private static Dimension getSize (Mass start, Mass end) {
        return new Dimension((int) start.distance(end), IMAGE_HEIGHT);
    }
}
