package simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Location;
import util.Pixmap;
import util.Sprite;
import util.Vector;
import view.Canvas;

/**
 * XXX.
 * 
 * @author Robert C. Duvall
 */
public class Mass extends Sprite implements SimulationObject {
	// reasonable default values
	public static final Dimension DEFAULT_SIZE = new Dimension(16, 16);
	public static final Pixmap DEFAULT_IMAGE = new Pixmap("mass.gif");

	private double myMass;
	private Vector myAcceleration;
	
	private Model model;
	private Environment environment;
	private Canvas canvas;
	
	private boolean[] toggleForces = new boolean[7];
	private Map<String, Integer> toggleMap = new HashMap<String, Integer>();
	private Map<Integer, String> toggleKeysMap = new HashMap<Integer, String>();
	private boolean canPress = true;

	/**
	 * XXX.
	 */
	public Mass(double x, double y, double mass, Model model) {
		super(DEFAULT_IMAGE, new Location(x, y), DEFAULT_SIZE);
		myMass = mass;
		myAcceleration = new Vector();
		this.model = model;
		this.environment = model.getEnvironment();
		this.canvas = model.getCanvas();
		setupToggleForces();
		printForces();
	}

	
	private void setupToggleForces() {

		String[] forcesList = {Keywords.GRAVITY_KEYWORD, Keywords.VISCOSITY_KEYWORD, Keywords.CENTER_OF_MASS_KEYWORD,
								Keywords.WALL_1, Keywords.WALL_2, Keywords.WALL_3, Keywords.WALL_4};
		
		int[] toggleKeys = {Keywords.TOGGLE_GRAVITY, Keywords.TOGGLE_VISCOSITY, Keywords.TOGGLE_CENTER_OF_MASS, 
				Keywords.TOGGLE_WALL_1, Keywords.TOGGLE_WALL_2, Keywords.TOGGLE_WALL_3, Keywords.TOGGLE_WALL_4};
		
		for (int i = 0; i < forcesList.length; i++) {
			toggleMap.put(forcesList[i], i);
			toggleKeysMap.put(toggleKeys[i], forcesList[i]);
		}
		
		//everything in toggleForces is false by default 
		
		for (int i = 0; i < forcesList.length; i++) {
			String keyword = forcesList[i];
			if (environment.getForce(keyword) != null) {
				toggleForces[toggleMap.get(keyword)] = true;
			}
		}
		
		//set up wallRepulsion 
		
		List<Force> wallRepulsionForces = environment.getWallForces(Keywords.WALL_KEYWORD);
		if (wallRepulsionForces != null) {
			for (Force wallForce : wallRepulsionForces) {
				int id = (int) Math.round(wallForce.getProperty("id"));
				toggleForces[id + 2] = true; //fix hard coded bit, but basically hacking around setting the wall forces 
			}
		}

	
	}

//    /**
//     * Paints the mass. Comment out to use .gifs in /images/
//     */
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
	 */
	@Override
	public void update(double elapsedTime, Dimension bounds) {
		
		applyToggle(canvas.getLastKeyPressed());
		applyForce(getBounce(bounds));
		applyEnvironment(environment);
	}
	
	
	private void applyToggle(int key) {
		String force = toggleKeysMap.get(key);
		if (force != null) {
			toggleForces[toggleMap.get(force)] = !toggleForces[toggleMap.get(force)];
			System.out.println("JUST TOGGLED A FORCE!!");
			printForces();
		}
		
		
	}
	
	private void printForces() {
		for (int i = 0; i < toggleForces.length; i++) {
			System.out.print("Entry " + i + ": " + toggleForces[i] + " ");
		}
		System.out.println();
	}
	

	/**
	 * Converts applied forces into velocity.
	 */
	@Override
	public void updateEnd(double elapsedTime, Dimension bounds) {
		// convert force back into Mover's velocity
		getVelocity().sum(myAcceleration);
		myAcceleration.reset();
		// move mass by velocity
		super.update(elapsedTime, bounds);
	}
	
//	/**
//	 * XXX.
//	 */
//	@Override
//	public void paint(Graphics2D pen) {
//		pen.setColor(Color.BLACK);
//		pen.fillOval((int) getLeft(), (int) getTop(), (int) getWidth(),
//				(int) getHeight());
//	}
	
	/**
	 * Applies environment forces.
	 */
	private void applyEnvironment(Environment env) {
		Vector gravity = getGravityVector(env);
		Vector centerMass = getCenterOfMassVector(env);
		Vector viscosity = getViscosityVector(env);
		Vector wallRepulsion = getWallRepulsionVector(env);
		applyForce(gravity);
		applyForce(centerMass);
		applyForce(viscosity);
		applyForce(wallRepulsion);
	}
	
	/**
	 * Returns the viscosity force on this mass.
	 */
	private Vector getViscosityVector(Environment env) {
		
		if (!toggleForces[toggleMap.get(Keywords.VISCOSITY_KEYWORD)]) {
			return new Vector();
		}
		
		Force viscosityForce = env.getForce(Keywords.VISCOSITY_KEYWORD);
		
		//here is the case where viscosity force has been toggled to true, yet it never existed 
		//we want to give it a default value 
		if (viscosityForce == null) { 
			return new Vector();
		}
		Vector viscosity = new Vector(getVelocity());
		double viscosityScale = viscosityForce.getProperty("scale");
		viscosity.setMagnitude(viscosity.getMagnitude() * viscosityScale);
		viscosity.turn(Keywords.DEGREES_TURN_AROUND);
		return viscosity;
	}
	
	/**
	 * Returns the gravity force on this mass.
	 */
	private Vector getGravityVector(Environment env) {
		
		if (!toggleForces[toggleMap.get(Keywords.GRAVITY_KEYWORD)]) {
			return new Vector();
		}
		
		Vector gravity = env.getVector(Keywords.GRAVITY_KEYWORD);
		
		//we want to give it a default value 
		if (gravity == null) {
			return new Vector();
		}
		gravity = new Vector(gravity);
		gravity.setMagnitude(myMass * gravity.getMagnitude());
		System.out.println("Gravity Vector mag: " + gravity.getMagnitude() + " angle: " + gravity.getAngle());
		return gravity;
	}
	
	/**
	 * Returns the center of mass force on this mass.
	 */
	private Vector getCenterOfMassVector(Environment env) {
		
		if (!toggleForces[toggleMap.get(Keywords.CENTER_OF_MASS_KEYWORD)]) {
			return new Vector();
		}
		
		Vector centerMassVector = env.getVector(Keywords.CENTER_OF_MASS_KEYWORD);
		Force centerMassForce = env.getForce(Keywords.CENTER_OF_MASS_KEYWORD);
		if (centerMassVector == null) {
			return new Vector();
		}
		centerMassVector = new Vector(centerMassVector);
		List<SimulationObject> objectList = model.getObjects();
		for (SimulationObject object : objectList) {
			if (!(object instanceof Mass)) {
				continue;
			}
			Mass mass = (Mass) object;
			if (object.equals(this)) {
				continue;
			}
			double otherX = mass.getX();
			double otherY = mass.getY();
			Location myCenter = new Location(getX(), getY());
			Location otherCenter = new Location(otherX, otherY);
			double distance = Vector.distanceBetween(myCenter, otherCenter);
			if (distance == Keywords.ZERO) {
				continue;
			}
			Vector currentCenterForce = new Vector();
			currentCenterForce.setMagnitude(centerMassForce.getProperty("magnitude") * Math.pow(
					(1.0 / distance), centerMassForce.getProperty("exponent")));
			double angle = Vector.angleBetween(myCenter, otherCenter);
			currentCenterForce.setAngle(angle);
			centerMassVector.sum(currentCenterForce);
		}
		return centerMassVector;
	}
		
	/**
	 * Returns the wall repulsion force on this mass.
	 */
	private Vector getWallRepulsionVector(Environment env) {
		List<Force> wallRepulsionForces = env.getWallForces(Keywords.WALL_KEYWORD);
		Vector wallRepulsionVector = new Vector();

		if (wallRepulsionForces == null) return new Vector();
		
		for (Force wallForce : wallRepulsionForces) {
			int id = (int) Math.round(wallForce.getProperty("id"));
			String wallToggleId = "wall" + id;
			
			if (!toggleForces[toggleMap.get(wallToggleId)]) {
				continue;
			}
			
			Location myCenter = new Location(getX(), getY());
			Location otherCenter = getWallLocation(id);
			double distance = Vector.distanceBetween(myCenter, otherCenter);
			
			if (distance == Keywords.ZERO) {
				continue;
			}
			
			Vector currentWallForce = new Vector();
			currentWallForce.setMagnitude(wallForce.getProperty("magnitude") * Math.pow(
					((double) Keywords.ONE / distance), wallForce.getProperty("exponent")));
					//this is the algorithm for calculating magnitude (same alg as for Center of Mass) 
			double angle = Vector.angleBetween(myCenter, otherCenter);
			currentWallForce.setAngle(angle);
			wallRepulsionVector.sum(currentWallForce);
			
//			System.out.println("My Mass is: " + this + 
//					" ; wall repulsion force is: " + wallRepulsionVector.getMagnitude() + " " +
//					"; angle is: " + wallRepulsionVector.getAngle() + " distance: " + distance);
		}

		return wallRepulsionVector;
	}
	
	
	
	private Location getWallLocation(int id) {
		double otherX, otherY;
		switch (id) {
		// wall is on TOP, so otherX = this.getX()
		case 1:
			otherX = this.getX();
			otherY = Keywords.ZERO;
			break;

		// wall is on the RIGHT
		case 2:
			otherX = Keywords.CANVAS_WIDTH; 
			otherY = this.getY();
			break;

		// wall is on the BOTTOM
		case 3:
			otherX = this.getX();
			otherY = Keywords.CANVAS_HEIGHT;
			break;

		// wall is on the LEFT
		case 4:
			otherX = Keywords.ZERO;
			otherY = this.getY();
			break;

		// default: set wall = current position
		default:
			otherX = this.getX();
			otherY = this.getY();
			break;
		}
		
		return new Location(otherX, otherY);
	}
	
	
	/**
	 * Convenience method.
	 */
	public double distance(Mass other) {
		// this is a little awkward, so hide it
		return new Location(getX(), getY()).distance(new Location(other.getX(), other.getY()));
	}
	
	public double getMass() {
		return myMass;
	}

}
