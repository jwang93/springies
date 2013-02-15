package simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import java.util.ArrayList;

import util.Location;
import util.Vector;
import view.Canvas;


/**
 * XXX.
 * 
 * @author Robert C. Duvall, Jay Wang, James Wei 
 */
public class Model {
    // bounds and input for game
    private Canvas myView;
    private Environment myEnvironment;
    // simulation state
    private List<SimulationObject> myObjects;
	private Dimension adjustableBounds;
	InvisibleMass myMass;
	Spring mySpring;
    public int count;

    /**
     * Create a game of the given size with the given display for its shapes.
     */
    public Model (Canvas canvas) {
        myView = canvas;
        myEnvironment = null;
        myObjects = new ArrayList<SimulationObject>();
        adjustableBounds = this.getBounds();
    }

    /**
     * Draw all elements of the simulation.
     */
    public void paint (Graphics2D pen) {
        for (SimulationObject o : myObjects) {
            o.paint(pen);
        }
        paint_bounds(pen);        
    }

    private void paint_bounds(Graphics2D pen) {
    	pen.setColor(Color.BLACK);
        pen.drawRect(0, 0, (int) adjustableBounds.getWidth(), (int) adjustableBounds.getHeight());
	}

	/**
     * Update simulation for this moment, given the time since the last moment.
     */
    public void update (double elapsedTime) {
    	boolean mousePressed = myView.mousePressed();
    	boolean mouseReleased = myView.mouseReleased();
		int lastKeyPressed = myView.getLastKeyPressed();
		
		applyAdjustBounds(lastKeyPressed);
		handleMouseDragging(mousePressed, mouseReleased);
		changeInvisibleMassLocation();
        for (SimulationObject o : myObjects) {
            o.update(elapsedTime, adjustableBounds, lastKeyPressed);
        }
        for (SimulationObject o : myObjects) {
            o.updateEnd(elapsedTime, adjustableBounds);
        }
    }

    private void changeInvisibleMassLocation() {
		if (myMass != null) {
			myMass.changeLocation(myView.getLastMousePosition());		
		}
	}

	private void handleMouseDragging(boolean mousePressed, boolean mouseReleased) {
		if (mousePressed == true) {
    		myMass = createInvisibleMass(mousePressed);
    		Mass nearestMass = getNearestMass(myMass); 
    		mySpring = new Spring (myMass, nearestMass, Keywords.DEFAULT_REST_LENGTH, Keywords.DEFAULT_K_VALUE);
    		myObjects.add(mySpring);
    	}
		
		if (mouseReleased == true) {
			myObjects.remove(myMass);
			myObjects.remove(mySpring);
		}
		
	}

	private Mass getNearestMass(Mass myMass) {
		Mass nearestMass = new Mass(0, 0, 0, this); 
		double distance = Double.MAX_VALUE;
		for (SimulationObject m : myObjects) {
			if (myMass.equals(m)) continue;
			if (!(m instanceof Mass)) continue;		
			double newDistance = distance(myMass, (Mass) m);
			if (newDistance < distance) {
				System.out.println("Going in");
				nearestMass = (Mass) m;
				distance = newDistance;
			}
		}
		return nearestMass;
	}

	public double distance(Mass mass1, Mass mass2) {
		// this is a little awkward, so hide it
		if (mass1.equals(null) ) return 0.0;
		return new Location(mass1.getX(), mass1.getY()).distance(new Location(mass2.getX(), mass2.getY()));
	}
	
    
	private InvisibleMass createInvisibleMass(boolean mousePressed) {
    	if (mousePressed) {
    		InvisibleMass mass = new InvisibleMass(myView.getLastMousePosition().getX(), myView.getLastMousePosition().getY(), Keywords.DEFAULT_MASS, this);
    		myObjects.add(mass);
    		return mass;
    	}
    	return null;
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
    
    /**
     * Returns the bounds
     */
    public Dimension getBounds() {
    	return myView.getSize();
    }
    
	private void applyAdjustBounds(int lastKeyPressed) {
		double currentHeight = adjustableBounds.getHeight();
		double currentWidth = adjustableBounds.getWidth();
		if (lastKeyPressed == Keywords.INCREASE_BOUNDS) {
			adjustableBounds.setSize(currentWidth + Keywords.PIXEL_ADJUSTMENT, currentHeight + Keywords.PIXEL_ADJUSTMENT);
			printBounds();
		}
		
		else if (lastKeyPressed == Keywords.DECREASE_BOUNDS && canvasLargeEnough() && massInBounds(currentWidth - Keywords.PIXEL_ADJUSTMENT, currentHeight - Keywords.PIXEL_ADJUSTMENT)) {
			adjustableBounds.setSize(currentWidth - Keywords.PIXEL_ADJUSTMENT, currentHeight - Keywords.PIXEL_ADJUSTMENT);
			printBounds();
		}
	}


	private boolean massInBounds(double projectedX, double projectedY) {
		for (SimulationObject obj : myObjects) {
			if (obj instanceof Mass) {
				Mass mass = (Mass) obj;
				if (mass.getX() > projectedX || mass.getY() > projectedY) {
					return false;
				}
			}
		}
		return true;
	}


	private boolean canvasLargeEnough() {
		if (adjustableBounds.getHeight() > 10.0 && adjustableBounds.getWidth() > 10.0) {
			return true;
		}
		return false;
	}
    
	private void printBounds() {
		System.out.println("Bounds: Height: " + adjustableBounds.getHeight() + " Width: " + adjustableBounds.getWidth());
		System.out.println();
	}
}
