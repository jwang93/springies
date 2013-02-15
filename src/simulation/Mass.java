package simulation;

import java.awt.Dimension;
import util.Location;
import util.Pixmap;
import util.Sprite;
import util.Vector;


/**
 * XXX.
 * 
 * @author Jay Wang, James Wei
 */
public class Mass extends Sprite implements SimulationObject {
    // reasonable default values
    public static final Dimension DEFAULT_SIZE = new Dimension(16, 16);
    public static final Pixmap DEFAULT_IMAGE = new Pixmap("mass.gif");

    private double myMass;
    private Vector myAcceleration;

    private Model myModel;
    private Environment myEnvironment;
    private Dimension myView;

    /**
     * XXX.
     */
    public Mass (double x, double y, double mass, Model model) {
        super(DEFAULT_IMAGE, new Location(x, y), DEFAULT_SIZE);
        myMass = mass;
        myAcceleration = new Vector();
        myModel = model;
        myEnvironment = model.getEnvironment();
        myView = model.getBounds();
    }

    // /**
    // * Paints the mass. Comment out to use .gifs in /images/
    // */
    // @Override
    // public void paint (Graphics2D pen) {
    // pen.setColor(Color.BLACK);
    // pen.fillOval((int)getLeft(), (int)getTop(), (int)getWidth(), (int)getHeight());
    // }

    /**
     * Use the given force to change this mass's acceleration.
     */
    public void applyForce (Vector force) {
        myAcceleration.sum(force);
    }

    // check for move out of bounds
    private Vector getBounce (Dimension bounds) {
        final double IMPULSE_MAGNITUDE = 2;
        Vector impulse = new Vector();
        if (getLeft() < Keywords.ZERO) {
            impulse = new Vector(RIGHT_DIRECTION, IMPULSE_MAGNITUDE);
        }
        else if (getRight() > bounds.width) {
            impulse = new Vector(LEFT_DIRECTION, IMPULSE_MAGNITUDE);
        }
        if (getTop() < Keywords.ZERO) {
            impulse = new Vector(DOWN_DIRECTION, IMPULSE_MAGNITUDE);
        }
        else if (getBottom() > bounds.height) {
            impulse = new Vector(UP_DIRECTION, IMPULSE_MAGNITUDE);
        }
        impulse.scale(getVelocity().getRelativeMagnitude(impulse));
        return impulse;
    }

    /**
     * Applies environment and bounce forces.
     * 
     * @param elapsedTime
     * @param bounds
     * @param lastKeyPressed
     */
    @Override
    public void update (double elapsedTime, Dimension bounds, int lastKeyPressed) {
        myView = bounds;
        applyForce(getBounce(bounds));
        applyEnvironment(myEnvironment);
    }

    /**
     * Converts applied forces into velocity.
     * 
     * @param elapsedTime
     * @param bounds
     */
    @Override
    public void updateEnd (double elapsedTime, Dimension bounds) {
        // convert force back into Mover's velocity
        getVelocity().sum(myAcceleration);
        myAcceleration.reset();
        // move mass by velocity
        super.update(elapsedTime, bounds);
    }

    /**
     * Applies environment forces.
     */
    private void applyEnvironment (Environment env) {
        applyForce(env.getTotalForce(this));
    }

    /**
     * Convenience method for finding the distance between this mass and another.
     */
    public double distance (Mass other) {
        return new Location(getX(), getY()).distance(new Location(other.getX(), other.getY()));
    }

    /**
     * Returns the mass (weight) of this mass object.
     */
    public double getMass () {
        return myMass;
    }

    /**
     * Returns the model associated with this mass.
     */
    public Model getModel () {
        return myModel;
    }

    /**
     * Returns the view represented as a Dimension associated with this mass.
     */
    public Dimension getView () {
        return myView;
    }

}
