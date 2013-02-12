package simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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
 * @author Jay Wang, James Wei
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
	private Dimension adjustableBounds;
	
	private boolean[] toggleForces = new boolean[7];
	private Map<String, Integer> toggleMap = new HashMap<String, Integer>();
	private Map<Integer, String> toggleKeysMap = new HashMap<Integer, String>();

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
		adjustableBounds = model.getBounds();
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
		int lastKeyPressed = canvas.getLastKeyPressed();
		applyAdjustBounds(lastKeyPressed);
		applyToggle(lastKeyPressed);
		applyForce(getBounce(adjustableBounds));
		applyEnvironment(environment);
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
		if (this.getX() < projectedX && this.getY() < projectedY) {
			return true;
		}
		return false;
	}


	private boolean canvasLargeEnough() {
		if (adjustableBounds.getHeight() > 10.0 && adjustableBounds.getWidth() > 10.0) {
			return true;
		}
		return false;
	}


	private void applyToggle(int key) {
		String force = toggleKeysMap.get(key);
		if (force != null) {
			toggleForces[toggleMap.get(force)] = !toggleForces[toggleMap.get(force)];
			System.out.println("JUST TOGGLED A FORCE!!");
			printForces();
		}
		
		
	}
	
	private void printBounds() {
		System.out.println("Bounds: Height: " + adjustableBounds.getHeight() + " Width: " + adjustableBounds.getWidth());
		System.out.println();
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

		Force viscosityForce = env.getForce(Keywords.VISCOSITY_KEYWORD);

		/**
		 * Case I: Viscosity has been turned off 
		 */
		if (!toggleForces[toggleMap.get(Keywords.VISCOSITY_KEYWORD)]) {
			return new Vector();
		}
		
		
		/**
		 * Case II: Viscosity has been turned on, but it had no initial value 
		 */
		if (viscosityForce == null) { 
			double defaultViscosityScale = Keywords.DEFAULT_VISCOSITY_SCALE; 
			Vector viscosity = new Vector(getVelocity());
			viscosity.setMagnitude(viscosity.getMagnitude() * defaultViscosityScale);
			viscosity.turn(Keywords.DEGREES_TURN_AROUND);
			return viscosity;
		}
		
		/**
		 * Case III: Viscosity has been turned on, and it does have an initial value
		 */
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
		
		/**
		 * Case I: Gravity has been turned off 
		 */
		if (!toggleForces[toggleMap.get(Keywords.GRAVITY_KEYWORD)]) {
			return new Vector();
		}
		
		Vector gravity = env.getVector(Keywords.GRAVITY_KEYWORD);
		
		/**
		 * Case II: Gravity has been turned on, but it had no initial value 
		 */
		if (gravity == null) {
			double defaultGravityMagnitude = Keywords.DEFAULT_GRAVITY_MAGNITUDE;
			double defaultGravityAngle = Keywords.DEFAULT_GRAVITY_ANGLE;
			Vector myGravity = new Vector(defaultGravityAngle, defaultGravityMagnitude);
			myGravity.setMagnitude(myMass * myGravity.getMagnitude());
			return myGravity;
		}
		
		/**
		 * Case III: Gravity has been turned on, and it does have an initial value
		 */
		else {
			gravity = new Vector(gravity);
			gravity.setMagnitude(myMass * gravity.getMagnitude());
			//System.out.println("Gravity Vector mag: " + gravity.getMagnitude() + " angle: " + gravity.getAngle());
			return gravity;
		}
	}
	
	/**
	 * Returns the center of mass force on this mass.
	 */
	private Vector getCenterOfMassVector(Environment env) {
		
		Vector centerMassVector = env.getVector(Keywords.CENTER_OF_MASS_KEYWORD);
		Force centerMassForce = env.getForce(Keywords.CENTER_OF_MASS_KEYWORD);
		
		/**
		 * Case I: COM has been turned off 
		 */
		if (!toggleForces[toggleMap.get(Keywords.CENTER_OF_MASS_KEYWORD)]) {
			return new Vector();
		}
			
		/**
		 * Case II: COM has been turned on, but it had no initial value 
		 */
		
		if (centerMassVector == null) {
			Vector retVector = new Vector();
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
				currentCenterForce.setMagnitude(Keywords.DEFAULT_CENTER_OF_MASS_MAGNITUDE * Math.pow(
						(1.0 / distance), Keywords.DEFAULT_CENTER_OF_MASS_EXPONENT));
				double angle = Vector.angleBetween(myCenter, otherCenter);
				currentCenterForce.setAngle(angle);
				retVector.sum(currentCenterForce);
			}
			return retVector;
		}
		
		/**
		 * Case III: COM has been turned on, and it does have an initial value
		 */
		else {
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

	}
		
	/**
	 * Returns the wall repulsion force on this mass.
	 */
	private Vector getWallRepulsionVector(Environment env) {
		List<Force> wallRepulsionForces = env.getWallForces(Keywords.WALL_KEYWORD);
		List<String> wallRepulsionForcesNames = new ArrayList<String>();
		
		for (Force wallRepulsionForce : wallRepulsionForces) {
			String wallForceName; 
			wallForceName = wallRepulsionForce.getName() + (int) Math.round(wallRepulsionForce.getProperty("id"));
			wallRepulsionForcesNames.add(wallForceName);
		}
		
		Vector wallRepulsionVector = new Vector();
		String[] wallList = {"wall1", "wall2", "wall3", "wall4"};
		
				
		for (String wall : wallList) {

			/**
			 * Case I: wall repulsion for this force has been turned off
			 */
			 
			if (!toggleForces[toggleMap.get(wall)]) {
				continue;
			}
			
			/**
			 * Case II: wall repulsion for this wall has been turned on, but no initial value
			 */
			
			if (!wallRepulsionForcesNames.contains(wall)) {
//				System.out.println("wallRepulsionForcesNames, size: " + wallRepulsionForcesNames.size());
//				System.out.println("Element is: " + wallRepulsionForcesNames.get(0));
//				System.out.println("Case 2 for wall:" + wall);
				int id = (int) wall.charAt(wall.length() - 1) - Keywords.ASCII_VALUE_ZERO;
				Location myCenter = new Location(getX(), getY());
				Location otherCenter = getWallLocation(id);
				double distance = Vector.distanceBetween(myCenter, otherCenter);
				
				if (distance == Keywords.ZERO) {
					continue;
				}
				
				Vector currentWallForce = new Vector();
				currentWallForce.setMagnitude(Keywords.DEFAULT_WALL_REPULSION_MAGNITUDE * Math.pow(
						((double) Keywords.ONE / distance), Keywords.DEFAULT_WALL_REPULSION_EXPONENT));
				
				double angle = Vector.angleBetween(myCenter, otherCenter);
				currentWallForce.setAngle(angle);
				wallRepulsionVector.sum(currentWallForce);
			}
			
			/**
			 * Case 3: wall repulsion for this wall turned on, yes initial value
			 * This means the force exists in wallRepulsionForces
			 */
			else {
				Force wallForce = new Force("wall");
				int id = (int) wall.charAt(wall.length() - 1) - Keywords.ASCII_VALUE_ZERO;
				for (Force force : wallRepulsionForces) {
					//System.out.println("Wall: " + wall + " My ID: " + id + " , their ID: " + (int) Math.round(force.getProperty("id")));
					if (id == (int) Math.round(force.getProperty("id"))) {
						wallForce = force;
						//System.out.println("HERE");
					}
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
			}

			

		}

//		System.out.println("My Mass is: " + this + 
//				" ; wall repulsion force is: " + wallRepulsionVector.getMagnitude() + " " +
//				"; angle is: " + wallRepulsionVector.getAngle());
		
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
