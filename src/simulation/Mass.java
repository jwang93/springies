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
    public static final Pixmap DEFUALT_IMAGE = new Pixmap("mass.gif");

    private double myMass;
    private Vector myAcceleration;
    private Vector myGravity;
    private Vector myViscosity; 
    private CenterOfMass myCenterOfMass = new CenterOfMass(1.3, 2.0);
    
    private Model model;

    /**
     * XXX.
     */
    public Mass (double x, double y, double mass, Model model) {
        super(DEFUALT_IMAGE, new Location(x, y), DEFAULT_SIZE);
        myMass = mass;
        myAcceleration = new Vector();
        this.model = model;
        myGravity = getGravity();
        myViscosity = getViscosity();
    }

    /**
     * XXX.
     */
    @Override
    public void update (double elapsedTime, Dimension bounds) {
        applyForce(getBounce(bounds));
        
        // GRAVITY FORCE --
        applyForce(myGravity);
        
        // VISCOSITY FORCE -- 
        applyForce(myViscosity);

        
        // CENTER OF MASS 
        /*
         * This mass' force doesn't get affected by Center of Mass
         * Rather, it applies a force on all the other Center of Masses. 
         * This is done through the method applyCenterOfMass() which is called in Model. 
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
    public void paint (Graphics2D pen) {
        pen.setColor(Color.BLACK);
        pen.fillOval((int)getLeft(), (int)getTop(), (int)getWidth(), (int)getHeight());
    }

    /**
     * Use the given force to change this mass's acceleration.
     */
    public void applyForce (Vector force) {
        myAcceleration.sum(force);
    }
    
    /**
     * Convenience method.
     */
    public double distance (Mass other) {
        // this is a little awkward, so hide it
        return new Location(getX(), getY()).distance(new Location(other.getX(), other.getY()));
    }


    private Gravity getGravity() {
    	Gravity newGravity = model.retGravity();
    	System.out.println("HELLO" + model.retGravity().getMagnitude());
    	myGravity.setMagnitude(myGravity.getMagnitude() * this.myMass);
    	System.out.println(myGravity.getMagnitude());
    	return newGravity;
    }


    /**
     * Needs to return the Viscosity vector 
     * This vector is basically: 
     * sum of all forces on the mass object * (-viscosity.getScale())
     * 
     * @return Vector that represents the Viscosity vector 
     */
    private Vector getViscosity() {
        return null;
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

	public void applyCenterOfMass(List<Mass> myMasses) {
		// TODO Auto-generated method stub
		
		
		/* 
		 * Ultimately, you are trying to create a new (Vector) Force that will be 
		 * applied to each mass in myMasses EXCEPT myself 
		 *  
		 * Need: 2 Points (both centers of mass), Distance between points  
		 * FORMULA for Magnitude: myCenterMassMagnitude*(1/distance)^centerExponentValue
		 * FORMULA for Angle: get from the Vector class - getAngle() , takes 2 points as parameters
		 * 
		 */
		double distance;
		Vector COM_force = new Vector();
		double myX = this.getX();
		double myY = this.getY();
		double otherX;
		double otherY;
		double magnitude;
		double angle;
		Point myCenter = new Point((int) myX, (int) myY);
		Point otherCenter = new Point(0, 0);  //initially set to default location of 0, 0
		
		for (Mass m : myMasses) {
			if (!this.equals(m)) {
				otherX = m.getX();
				otherY = m.getY();
				otherCenter.setLocation(otherX, otherY);
				distance = distanceBetween(myCenter, otherCenter);
				magnitude = (myCenterOfMass.getCOM_Mag() * Math.pow((1.0/distance), myCenterOfMass.getCOM_ExpVal()));
				angle = angleBetween(myCenter, otherCenter);
				COM_force.setMagnitude(magnitude);
				COM_force.setAngle(angle);
				applyForce(COM_force);
			}
		}
	}
	
	
    public static double distanceBetween (Point2D p1, Point2D p2) {
        return distanceBetween(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }
    
    public static double distanceBetween (double dx, double dy) {
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public static double angleBetween (Point2D p1, Point2D p2) {
        return angleBetween(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }

    public static double angleBetween (double dx, double dy) {
        return Math.toDegrees(Math.atan2(dy, dx));
    }
}
