package simulation;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;

import util.Vector;
import view.Canvas;


/**
 * XXX.
 * 
 * @author Robert C. Duvall
 */
public class Model {
    // bounds and input for game
    private Canvas myView;
    // simulation state
    private List<Mass> myMasses;
    private List<Spring> mySprings;
    private Gravity envGravity = new Gravity(0, 0);     //need to fix this public 
    private Viscosity envViscosity = new Viscosity(0);    // 0 means no viscosity 

    /**
     * Create a game of the given size with the given display for its shapes.
     */
    public Model (Canvas canvas) {
        myView = canvas;
        myMasses = new ArrayList<Mass>();
        mySprings = new ArrayList<Spring>();
    }

    /**
     * Draw all elements of the simulation.
     */
    public void paint (Graphics2D pen) {
        for (Spring s : mySprings) {
            s.paint(pen);
        }
        for (Mass m : myMasses) {
            m.paint(pen);
        }
    }

    /**
     * Update simulation for this moment, given the time since the last moment.
     */
    public void update (double elapsedTime) {
        Dimension bounds = myView.getSize();
        for (Spring s : mySprings) {
            s.update(elapsedTime, bounds);
        }
        for (Mass m : myMasses) {
            m.update(elapsedTime, bounds);
            m.applyCenterOfMass(myMasses);
        }
    }

    /**
     * Add given mass to this simulation.
     */
    public void add (Mass mass) {
        myMasses.add(mass);
    }

    /**
     * Add given spring to this simulation.
     */
    public void add (Spring spring) {
        mySprings.add(spring);
    }
    
    public void add (Gravity force) {
    	System.out.println("Gravity with magnitude of: " + force.getMagnitude() + " has been added.");
    	envGravity = force;
    }
    
    public void add (Viscosity visc) {
    	System.out.println("Viscosity of: " + visc.getScale() + " has been added.");
    	envViscosity = visc;
    }
    
    public Gravity retGravity() {
    	return envGravity;
    }
    
    public Viscosity retViscosity() {
    	return envViscosity;
    }
    
}
