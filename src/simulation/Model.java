package simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import util.Location;
import view.Canvas;


/**
 * XXX.
 * 
 * @author Robert C. Duvall, Jay Wang, James Wei
 */
public class Model {

    private InvisibleMass myMass;
    private Spring mySpring;
    // bounds and input for game
    private Canvas myView;
    private Environment myEnvironment;
    // simulation state
    private List<SimulationObject> myObjects;
    private Dimension myAdjustableBounds;

    /**
     * Create a game of the given size with the given display for its shapes.
     * @param canvas is the view associated with the model
     */
    public Model (Canvas canvas) {
        myView = canvas;
        myEnvironment = null;
        myObjects = new ArrayList<SimulationObject>();
        myAdjustableBounds = getBounds();
    }

    /**
     * Draw all elements of the simulation.
     * @param pen is the Graphics2D object used to paint on the canvas
     */
    public void paint (Graphics2D pen) {
        for (SimulationObject o : myObjects) {
            o.paint(pen);
        }
        paintBounds(pen);
    }

    /**
     * Draws the boundaries of the view.
     * @param pen is the Graphics2D object used to paint on the canvas
     */
    private void paintBounds (Graphics2D pen) {
        pen.setColor(Color.BLACK);
        pen.drawRect(0, 0, (int)myAdjustableBounds.getWidth(), (int)myAdjustableBounds.getHeight());
    }

    /**
     * Update simulation for this moment, given the time since the last moment.
     * @param elapsedTime is the time since last update
     */
    public void update (double elapsedTime) {
        boolean mousePressed = myView.mousePressed();
        boolean mouseReleased = myView.mouseReleased();
        int lastKeyPressed = myView.getLastKeyPressed();

        applyAdjustBounds(lastKeyPressed);
        applyMouseDragging(mousePressed, mouseReleased);
        applyForceToggle(lastKeyPressed);
        applyAssemblyLoad(lastKeyPressed);
        changeInvisibleMassLocation();
        for (SimulationObject o : myObjects) {
            o.update(elapsedTime, myAdjustableBounds, lastKeyPressed);
        }
        for (SimulationObject o : myObjects) {
            o.updateEnd(elapsedTime, myAdjustableBounds);
        }
    }

    private void changeInvisibleMassLocation () {
        if (myMass != null) {
            myMass.changeLocation(myView.getLastMousePosition());
        }
    }

    private void applyMouseDragging (boolean mousePressed, boolean mouseReleased) {
        if (mousePressed) {
            myMass = createInvisibleMass();
            Mass nearestMass = getNearestMass(myMass);
            mySpring =
                    new Spring(myMass, nearestMass, Keywords.DEFAULT_REST_LENGTH,
                               Keywords.DEFAULT_K_VALUE);
            myObjects.add(mySpring);
        }
        if (mouseReleased) {
            myObjects.remove(myMass);
            myObjects.remove(mySpring);
        }
    }

    /**
     * If the user hit any assembly hotkey, apply its effects here.
     */
    private void applyAssemblyLoad (int lastKeyPressed) {
        switch (lastKeyPressed) {
            case Keywords.ASSEMBLY_CLEAR:
                myObjects.clear();
                break;
            case Keywords.ASSEMBLY_LOAD:
                myView.loadModel();
                break;
            default:
                break;
        }
    }

    /**
     * If the user hit any force toggle hotkey, toggle the force here.
     */
    private void applyForceToggle (int lastKeyPressed) {
        Force force;
        switch (lastKeyPressed) {
            case Keywords.TOGGLE_GRAVITY:
                force = myEnvironment.findForce(new ForceGravity());
                force.setToggle(!force.getToggle());
                break;
            case Keywords.TOGGLE_CENTER_OF_MASS:
                force = myEnvironment.findForce(new ForceCenterMass());
                force.setToggle(!force.getToggle());
                break;
            case Keywords.TOGGLE_VISCOSITY:
                force = myEnvironment.findForce(new ForceViscosity());
                force.setToggle(!force.getToggle());
                break;
            case Keywords.TOGGLE_WALL_TOP:
                force = myEnvironment.findForce(new ForceWallRepulsion(Keywords.WALL_ID_TOP));
                force.setToggle(!force.getToggle());
                break;
            case Keywords.TOGGLE_WALL_RIGHT:
                force = myEnvironment.findForce(new ForceWallRepulsion(Keywords.WALL_ID_RIGHT));
                force.setToggle(!force.getToggle());
                break;
            case Keywords.TOGGLE_WALL_BOTTOM:
                force = myEnvironment.findForce(new ForceWallRepulsion(Keywords.WALL_ID_BOTTOM));
                force.setToggle(!force.getToggle());
                break;
            case Keywords.TOGGLE_WALL_LEFT:
                force = myEnvironment.findForce(new ForceWallRepulsion(Keywords.WALL_ID_LEFT));
                force.setToggle(!force.getToggle());
                break;
            default:
                break;
        }
    }

    private Mass getNearestMass (Mass mass) {
        Mass nearestMass = new Mass(0, 0, 0, this);
        double distance = Double.MAX_VALUE;
        for (SimulationObject m : myObjects) {
            if (mass.equals(m)) {
                continue;
            }
            if (!(m instanceof Mass)) {
                continue;
            }
            double newDistance = distance(mass, (Mass) m);
            if (newDistance < distance) {
                nearestMass = (Mass) m;
                distance = newDistance;
            }
        }
        return nearestMass;
    }

    /**
     * Returns the distance between two masses.
     * @param mass1 is the first mass
     * @param mass2 is the second mass
     */
    public double distance (Mass mass1, Mass mass2) {
        // this is a little awkward, so hide it
        if (mass1.equals(null)) {
            return 0.0;
        }
        Location firstLocation = new Location(mass1.getX(), mass1.getY());
        Location secondLocation = new Location(mass2.getX(), mass2.getY());
        return firstLocation.distance(secondLocation);
    }

    private InvisibleMass createInvisibleMass () {
        double x = myView.getLastMousePosition().getX();
        double y = myView.getLastMousePosition().getY();
        InvisibleMass mass = new InvisibleMass(x, y, Keywords.DEFAULT_MASS, this);
        myObjects.add(mass);
        return mass;
    }

    /**
     * Add given object to this simulation.
     * @param object is the object to add
     */
    public void add (SimulationObject object) {
        myObjects.add(object);
    }

    /**
     * Returns list of all objects in model.
     */
    public List<SimulationObject> getObjects () {
        return myObjects;
    }

    /**
     * Returns the environment.
     */
    public Environment getEnvironment () {
        return myEnvironment;
    }

    /**
     * Sets the environment.
     * @param env is the environment to set
     */
    public void setEnvironment (Environment env) {
        myEnvironment = env;
    }

    /**
     * Returns the canvas.
     */
    public Canvas getCanvas () {
        return myView;
    }

    /**
     * Returns the bounds
     */
    public Dimension getBounds () {
        return myView.getSize();
    }

    private void applyAdjustBounds (int lastKeyPressed) {
        double currentHeight = myAdjustableBounds.getHeight();
        double currentWidth = myAdjustableBounds.getWidth();
        if (lastKeyPressed == Keywords.INCREASE_BOUNDS) {
            myAdjustableBounds.setSize(currentWidth + Keywords.PIXEL_ADJUSTMENT,
                                     currentHeight + Keywords.PIXEL_ADJUSTMENT);
        }
        else if (lastKeyPressed == Keywords.DECREASE_BOUNDS &&
                 canvasLargeEnough() &&
                 massInBounds(currentWidth - Keywords.PIXEL_ADJUSTMENT, currentHeight -
                                                                        Keywords.PIXEL_ADJUSTMENT)) {
            myAdjustableBounds.setSize(currentWidth - Keywords.PIXEL_ADJUSTMENT,
                                     currentHeight - Keywords.PIXEL_ADJUSTMENT);
        }
    }

    private boolean massInBounds (double projectedX, double projectedY) {
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

    private boolean canvasLargeEnough () {
        double h = myAdjustableBounds.getHeight();
        double w = myAdjustableBounds.getWidth();
        return h > Keywords.MIN_BOUNDS_HEIGHT && w > Keywords.MIN_BOUNDS_WIDTH;
    }

    // private void printBounds() {
    // System.out.println("Bounds: Height: " + adjustableBounds.getHeight() + " Width: " +
    // adjustableBounds.getWidth());
    // System.out.println();
    // }
}
