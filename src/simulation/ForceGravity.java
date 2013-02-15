package simulation;

import java.util.Scanner;
import util.Vector;


public class ForceGravity extends Force {

    public ForceGravity () {
        super();
        addProperty(Keywords.DIRECTION, Keywords.DEFAULT_GRAVITY_DIRECTION);
        addProperty(Keywords.MAGNITUDE, Keywords.DEFAULT_GRAVITY_MAGNITUDE);
        setDefaultVector();
        setName(Keywords.GRAVITY_KEYWORD);
    }

    public ForceGravity (Scanner line) {
        super();
        double myDirection = line.nextDouble();
        double myMagnitude = line.nextDouble();
        addProperty(Keywords.DIRECTION, myDirection);
        addProperty(Keywords.MAGNITUDE, myMagnitude);
        setToggle(true);
        setDefaultVector();
        setName(Keywords.GRAVITY_KEYWORD);
    }

    @Override
    public Vector getForceOnMass (Mass m) {
        if (!getToggle()) return new Vector();
        Vector result = new Vector(getDefaultVector());
        result.setMagnitude(m.getMass() * result.getMagnitude());
        return result;
    }

}
