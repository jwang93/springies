package simulation;

import java.util.Scanner;
import util.Location;
import util.Vector;


public class ForceWallRepulsion extends Force {

    public ForceWallRepulsion (int id) {
        super();
        addProperty(Keywords.ID, (double) id);
        addProperty(Keywords.MAGNITUDE, Keywords.DEFAULT_WALL_REPULSION_MAGNITUDE);
        addProperty(Keywords.EXPONENT, Keywords.DEFAULT_WALL_REPULSION_EXPONENT);
        setDefaultVector();
        setName(Keywords.WALL_KEYWORD);
    }

    public ForceWallRepulsion (Scanner line) {
        super();
        int myID = line.nextInt();
        double myMagnitude = line.nextDouble();
        double myExponent = line.nextDouble();
        addProperty(Keywords.ID, (double) myID);
        addProperty(Keywords.MAGNITUDE, myMagnitude);
        addProperty(Keywords.EXPONENT, myExponent);
        setToggle(true);
        setDefaultVector();
        setName(Keywords.WALL_KEYWORD);
    }

    @Override
    public Vector getForceOnMass (Mass m) {
        if (!getToggle()) return new Vector();
        Location myCenter = new Location(m.getX(), m.getY());
        Location otherCenter = getWallLocation(m, (int) getProperty(Keywords.ID).doubleValue());
        double distance = Vector.distanceBetween(myCenter, otherCenter);
        if (distance == Keywords.ZERO) return new Vector();
        Vector result = new Vector(getDefaultVector());
        result.setMagnitude(result.getMagnitude() *
                            Math.pow(
                                     (Keywords.ONE / distance), getProperty(Keywords.EXPONENT)));
        double angle = Vector.angleBetween(myCenter, otherCenter);
        result.setAngle(angle);
        return result;
    }

    /**
     * Returns the wall id of this wall repulsion force.
     * 
     * @return
     */
    public double getID () {
        return getProperty(Keywords.ID);
    }

    /**
     * Overwrites the Force equals method to also consider wall ID.
     */
    @Override
    public boolean equals (Force other) {
        if (super.equals(other))
            return getProperty(Keywords.ID).equals(other.getProperty(Keywords.ID));
        return false;
    }

    /**
     * Overwrites the Force hashCode method to also consider wall ID.
     */
    @Override
    public int hashCode () {
        return super.hashCode() + (int) getProperty(Keywords.ID).doubleValue();
    }

    /**
     * Convenience method for getting the location of the wall given its ID.
     * Wall IDs are defined in the Springies Part 2 outline.
     * http://www.cs.duke.edu/courses/spring13/compsci308/assign/02_springies/part2.php
     */
    private Location getWallLocation (Mass m, int id) {
        double otherX, otherY;
        switch (id) {
            case Keywords.WALL_ID_TOP:
                otherX = m.getX();
                otherY = Keywords.ZERO;
                break;
            case Keywords.WALL_ID_RIGHT:
                otherX = m.getView().getWidth();
                otherY = m.getY();
                break;
            case Keywords.WALL_ID_BOTTOM:
                otherX = m.getX();
                otherY = m.getView().getHeight();
                break;
            case Keywords.WALL_ID_LEFT:
                otherX = Keywords.ZERO;
                otherY = m.getY();
                break;

            // default: set wall = current position
            default:
                otherX = m.getX();
                otherY = m.getY();
                break;
        }
        return new Location(otherX, otherY);
    }

}
