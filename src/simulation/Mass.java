package simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

import util.Location;
import util.Pixmap;
import util.Sprite;
import util.Vector;

/**
 * XXX.
 * 
 * @author Robert C. Duvall
 */
public class Mass extends Sprite {
	// reasonable default values
	public static final Dimension DEFAULT_SIZE = new Dimension(16, 16);
	public static final Pixmap DEFAULT_IMAGE = new Pixmap("mass.gif");

	private double myMass;
	private Vector myAcceleration;
	
	private Model model;
	private Environment environment;

	/**
	 * XXX.
	 */
	public Mass(double x, double y, double mass, Model model) {
		super(DEFAULT_IMAGE, new Location(x, y), DEFAULT_SIZE);
		myMass = mass;
		myAcceleration = new Vector();
		this.model = model;
		this.environment = model.getEnvironment();
	}

    /**
     * XXX.
     */
//    @Override
//    public void paint (Graphics2D pen) {
//        pen.setColor(Color.BLACK);
//        pen.fillOval((int)getLeft(), (int)getTop(), (int)getWidth(), (int)getHeight());
//    }

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
        if (getLeft() < 0) {
            impulse = new Vector(RIGHT_DIRECTION, IMPULSE_MAGNITUDE);
        }
        else if (getRight() > bounds.width) {
            impulse = new Vector(LEFT_DIRECTION, IMPULSE_MAGNITUDE);
        }
        if (getTop() < 0) {
            impulse = new Vector(DOWN_DIRECTION, IMPULSE_MAGNITUDE);
        }
        else if (getBottom() > bounds.height) {
            impulse = new Vector(UP_DIRECTION, IMPULSE_MAGNITUDE);
        }
        impulse.scale(getVelocity().getRelativeMagnitude(impulse));
        return impulse;
    }

	/**
	 * XXX.
	 */
	@Override
	public void update(double elapsedTime, Dimension bounds) {
		applyForce(getBounce(bounds));
		applyEnvironment(environment);

		// CENTER OF MASS
		/*
		 * This mass' force doesn't get affected by Center of Mass Rather, it
		 * applies a force on all the other Center of Masses. This is done
		 * through the method applyCenterOfMass() which is called in Model.
		 */

		// convert force back into Mover's velocity
		getVelocity().sum(myAcceleration);
		myAcceleration.reset();
		// move mass by velocity
		super.update(elapsedTime, bounds);
	}

	/**
	 * XXX.
	 */
	@Override
	public void paint(Graphics2D pen) {
		pen.setColor(Color.BLACK);
		pen.fillOval((int) getLeft(), (int) getTop(), (int) getWidth(),
				(int) getHeight());
	}
	
	/**
	 * Applies environment forces.
	 */
	public void applyEnvironment(Environment env) {
		Vector gravity = getGravityVector(env);
		Vector centerMass = getCenterOfMassVector(env);
		Vector viscosity = getViscosityVector(env);
		applyForce(gravity);
		applyForce(centerMass);
		applyForce(viscosity);
	}
	
	/**
	 * Returns the viscosity force on this mass.
	 */
	private Vector getViscosityVector(Environment env) {
		Force viscosityForce = env.getForce(Keywords.VISCOSITY_KEYWORD);
		if (viscosityForce == null) {
			return new Vector();
		}
		Vector viscosity = new Vector(getVelocity());
		double viscosityScale = viscosityForce.getProperty("scale");
		viscosity.setMagnitude(viscosity.getMagnitude() * viscosityScale);
		viscosity.turn(180);
		return viscosity;
	}
	
	/**
	 * Returns the gravity force on this mass.
	 */
	private Vector getGravityVector(Environment env) {
		Vector gravity = env.getVector(Keywords.GRAVITY_KEYWORD);
		if (gravity == null) {
			return new Vector();
		}
		gravity = new Vector(gravity);
		gravity.setMagnitude(myMass * gravity.getMagnitude());
		return gravity;
	}
	
	/**
	 * Returns the center of mass force on this mass.
	 */
	private Vector getCenterOfMassVector(Environment env) {
		Vector centerMassVector = env.getVector(Keywords.CENTER_OF_MASS_KEYWORD);
		Force centerMassForce = env.getForce(Keywords.CENTER_OF_MASS_KEYWORD);
		if (centerMassVector == null) {
			return new Vector();
		}
		centerMassVector = new Vector(centerMassVector);
		List<Mass> massList = model.getMasses();
		for (Mass mass : massList) {
			if (mass.equals(this)) {
				continue;
			}
			double otherX = mass.getX();
			double otherY = mass.getY();
			Location myCenter = new Location(getX(), getY());
			Location otherCenter = new Location(otherX, otherY);
			double distance = distanceBetween(myCenter, otherCenter);
			if (distance == 0) {
				continue;
			}
			Vector currentCenterForce = new Vector();
			currentCenterForce.setMagnitude(centerMassForce.getProperty("magnitude") * Math.pow(
					(1.0 / distance), centerMassForce.getProperty("exponent")));
			double angle = angleBetween(myCenter, otherCenter);
			currentCenterForce.setAngle(angle);
			centerMassVector.sum(currentCenterForce);
		}
		return centerMassVector;
	}
	
	/**
	 * Convenience method.
	 */
	public double distance(Mass other) {
		// this is a little awkward, so hide it
		return new Location(getX(), getY()).distance(new Location(other.getX(),
				other.getY()));
	}
	
	public double getMass() {
		return myMass;
	}

	private static double distanceBetween(Point2D p1, Point2D p2) {
		return distanceBetween(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}

	private static double distanceBetween(double dx, double dy) {
		return Math.sqrt(dx * dx + dy * dy);
	}

	private static double angleBetween(Point2D p1, Point2D p2) {
		return angleBetween(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}

	private static double angleBetween(double dx, double dy) {
		return Math.toDegrees(Math.atan2(dy, dx));
	}

}
