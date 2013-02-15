package simulation;

import java.util.Scanner;
import util.Vector;


public class ForceViscosity extends Force {

    public ForceViscosity () {
        super();
        addProperty(Keywords.SCALE, Keywords.DEFAULT_VISCOSITY_SCALE);
        setDefaultVector();
        setName(Keywords.VISCOSITY_KEYWORD);
    }

    public ForceViscosity (Scanner line) {
        super();
        double myScale = line.nextDouble();
        addProperty(Keywords.SCALE, myScale);
        setToggle(true);
        setDefaultVector();
        setName(Keywords.VISCOSITY_KEYWORD);
    }

    @Override
    public Vector getForceOnMass (Mass m) {
        if (!getToggle()) return new Vector();
        Vector result = new Vector(m.getVelocity());
        double viscosityScale = getProperty(Keywords.SCALE);
        result.setMagnitude(result.getMagnitude() * viscosityScale);
        result.turn(Keywords.DEGREES_TURN_AROUND);
        return result;
    }

}
