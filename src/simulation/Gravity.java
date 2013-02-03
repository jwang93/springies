package simulation;

import util.Vector;

public class Gravity extends Vector {
	
    public Gravity (double angle, double magnitude) {
        setDirection(angle);
        setMagnitude(magnitude);
    }


}
