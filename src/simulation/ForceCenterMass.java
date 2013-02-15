package simulation;

import java.util.List;
import java.util.Scanner;
import util.Location;
import util.Vector;


public class ForceCenterMass extends Force {

    public ForceCenterMass () {
        super();
        addProperty(Keywords.MAGNITUDE, Keywords.DEFAULT_CENTER_OF_MASS_MAGNITUDE);
        addProperty(Keywords.EXPONENT, Keywords.DEFAULT_CENTER_OF_MASS_EXPONENT);
        setDefaultVector();
        setName(Keywords.CENTER_OF_MASS_KEYWORD);
    }

    public ForceCenterMass (Scanner line) {
        super();
        double myMagnitude = line.nextDouble();
        double myExponent = line.nextDouble();
        addProperty(Keywords.MAGNITUDE, myMagnitude);
        addProperty(Keywords.EXPONENT, myExponent);
        setToggle(true);
        setDefaultVector();
        setName(Keywords.CENTER_OF_MASS_KEYWORD);
    }

    @Override
    public Vector getForceOnMass (Mass m) {
        if (!getToggle()) return new Vector();
        Vector result = new Vector(getDefaultVector());
        List<SimulationObject> objectList = m.getModel().getObjects();
        for (SimulationObject object : objectList) {
            if (!(object instanceof Mass)) {
                continue;
            }
            Mass mass = (Mass) object;
            if (object.equals(this)) {
                continue;
            }
            double otherX = mass.getX();
            double otherY = mass.getY();
            Location myCenter = new Location(m.getX(), m.getY());
            Location otherCenter = new Location(otherX, otherY);
            double distance = Vector.distanceBetween(myCenter, otherCenter);
            if (distance == Keywords.ZERO) {
                continue;
            }
            Vector currentCenterForce = new Vector();
            currentCenterForce.setMagnitude(getProperty(Keywords.MAGNITUDE) *
                                            Math.pow(
                                                     (1.0 / distance),
                                                     getProperty(Keywords.EXPONENT)));
            double angle = Vector.angleBetween(myCenter, otherCenter);
            currentCenterForce.setAngle(angle);
            result.sum(currentCenterForce);
        }
        return result;
    }

}
