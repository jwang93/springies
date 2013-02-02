package simulation;

import java.awt.Dimension;

import util.Vector;

public class FixedMass extends Mass {

	public FixedMass(double x, double y, double mass, Model model) {
		super(x, y, mass, model);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public void update (double elapsedTime, Dimension bounds) {
		//do nothing
    }
    
    @Override
    public void applyForce (Vector force) {
    	//do nothing because nothing affects me 
    }

}