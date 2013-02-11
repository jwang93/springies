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
    private Environment myEnvironment;
    // simulation state
    private List<SimulationObject> myObjects;
    public int count;

    /**
     * Create a game of the given size with the given display for its shapes.
     */
    public Model (Canvas canvas) {
        myView = canvas;
        myEnvironment = null;
        myObjects = new ArrayList<SimulationObject>();
    }

    /**
     * Draw all elements of the simulation.
     */
    public void paint (Graphics2D pen) {
        for (SimulationObject o : myObjects) {
            o.paint(pen);
        }
    }

    /**
     * Update simulation for this moment, given the time since the last moment.
     */
    public void update (double elapsedTime) {
    	count++;
        Dimension bounds = myView.getSize();
        for (SimulationObject o : myObjects) {
            o.update(elapsedTime, bounds);
        }
        for (SimulationObject o : myObjects) {
            o.updateEnd(elapsedTime, bounds);
        }
    }

    /**
     * Add given object to this simulation.
     */
    public void add (SimulationObject object) {
        myObjects.add(object);
    }
    
    /**
     * Returns list of all masses in model.
     */
    public List<SimulationObject> getObjects() {
    	return myObjects;
    }
    
    /**
     * Returns the environment.
     */
    public Environment getEnvironment() {
    	return myEnvironment;
    }
    
    /**
     * Sets the environment.
     */
    public void setEnvironment(Environment env) {
    	myEnvironment = env;
    }
    
    /**
     * Returns the canvas.
     */
    public Canvas getCanvas() {
    	return myView;
    }
    
}
