package simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import util.Vector;

public class Environment {

	private static final HashMap<String,Force> myForces = new HashMap<String,Force>();
	private static final HashMap<String,Vector> myVectors = new HashMap<String,Vector>();
	
	private static final List<Force> wallForces = new ArrayList<Force>();
	private static final HashMap<String, List<Force>> wallForcesMap = new HashMap<String, List<Force>>();
	private static final HashMap<String, List<Vector>> wallVectorsMap = new HashMap<String, List<Vector>>();
	private static final List<Vector> wallVectors = new ArrayList<Vector>();
	
	private static final String DEFAULT_ENV_FILE = "src/data/environment.xsp";

	public Environment() {
		File file = new File(DEFAULT_ENV_FILE);
		loadEnvironment(file);
	}
	
	/**
	 * Load the environment and its forces.
	 */
    public void loadEnvironment (File file) {
    	try {
    		Scanner input = new Scanner(file);
    		while (input.hasNext()) {
    			Scanner line = new Scanner(input.nextLine());
    			if (line.hasNext()) {
    				String type = line.next();
    				if (Keywords.GRAVITY_KEYWORD.equals(type)) {
    					addForce(Keywords.GRAVITY_KEYWORD, gravityCommand(line));
    			        addVector(Keywords.GRAVITY_KEYWORD);
    				} 
    				else if (Keywords.VISCOSITY_KEYWORD.equals(type)) {
    					addForce(Keywords.VISCOSITY_KEYWORD, viscosityCommand(line));
    			        addVector(Keywords.VISCOSITY_KEYWORD);
    				}
    				else if (Keywords.CENTER_OF_MASS_KEYWORD.equals(type)) {
    					addForce(Keywords.CENTER_OF_MASS_KEYWORD, centerMassCommand(line));
    					addVector(Keywords.CENTER_OF_MASS_KEYWORD);
    				}
    				// we can't title the Keyword.Wall_Keyword 
    				// we could do a case switch statement here... 
    				else if (Keywords.WALL_KEYWORD.equals(type)) {
    					addWallForce(Keywords.WALL_KEYWORD, wallCommand(line));
    					addWallVector(Keywords.WALL_KEYWORD);
    				}
    			}
    		}
    		input.close();
    	}
    	catch (FileNotFoundException e) {
    		System.err.printf("environment.xsp file not found, please check %s", DEFAULT_ENV_FILE);
    		e.printStackTrace();
    	}
    }
    
    /**
     * Adds a gravity force.
     */
    private Force gravityCommand (Scanner line) {
        double myDirection = line.nextDouble();
        double myMagnitude = line.nextDouble();
        Force gravity = new Force(Keywords.GRAVITY_KEYWORD);
        gravity.addProperty("direction", myDirection);
        gravity.addProperty("magnitude", myMagnitude);
        return gravity;
    }
    
    /**
     * Adds a viscosity force.
     */
    private Force viscosityCommand (Scanner line) {
        double myScale = line.nextDouble();
        Force viscosity = new Force(Keywords.VISCOSITY_KEYWORD);
        viscosity.addProperty("scale", myScale);
        return viscosity;
    }
    
    /**
     * Adds a center of mass force.
     */
    private Force centerMassCommand (Scanner line) {
        double myMagnitude = line.nextDouble();
        double myExponent = line.nextDouble();
        Force centerMass = new Force(Keywords.CENTER_OF_MASS_KEYWORD);
        centerMass.addProperty("magnitude", myMagnitude);
        centerMass.addProperty("exponent", myExponent);
        return centerMass;
    }
    
    /**
     * Adds a wall force.
     */
    private Force wallCommand (Scanner line) {
    	int myID = line.nextInt();
        double myMagnitude = line.nextDouble();
        double myExponent = line.nextDouble();
        Force wallRepulsion = new Force(Keywords.WALL_KEYWORD);
        wallRepulsion.addProperty("id", (double) myID);
        wallRepulsion.addProperty("magnitude", myMagnitude);
        wallRepulsion.addProperty("exponent", myExponent);
        return wallRepulsion;
    }
	
    /**
     * Creates a vector from the force map and adds to the vector map.
     */
    private void addVector(String forceName) {
    	if (!myForces.containsKey(forceName)) {
    		System.err.printf("WARNING: %s does not exist in the force map", forceName);
    		return;
    	}
    	Force force = myForces.get(forceName);
    	Vector vector = new Vector();
    	if (force.getProperty("magnitude") != null) {
    		vector.setMagnitude(force.getProperty("magnitude"));
    	} else {
    		vector.setMagnitude(0);
    	}
    	if (force.getProperty("direction") != null) {
    		vector.setAngle(force.getProperty("direction"));
    	} else {
    		vector.setAngle(0);
    	}
    	myVectors.put(forceName, vector);
    }
    
    
    /**
     * Creates a vector from the force map and adds to the wall vector map.
     */
    private void addWallVector(String forceName) {
    	if (!wallForcesMap.containsKey(forceName)) {
    		System.err.printf("WARNING: %s does not exist in the force map", forceName);
    		return;
    	}
    	Vector vector = new Vector();
    	Force lastForce = wallForces.get(wallForces.size() - 1);
    	
    	if (lastForce.getProperty("magnitude") != null) {
    		vector.setMagnitude(lastForce.getProperty("magnitude"));
    	} else {
    		vector.setMagnitude(0);
    	}
    	if (lastForce.getProperty("direction") != null) {
    		vector.setAngle(lastForce.getProperty("direction"));
    	} else {
    		vector.setAngle(0);
    	}
    	
    	wallVectors.add(vector);
    	wallVectorsMap.put(forceName, wallVectors);
    }
    
    /**
     * Adds a force to the map, or updates it if it exists.
     */
    private void addForce(String name, Force force) {
    	myForces.put(name, force);
    }
    
    /**
     * Adds a wall force to the map, or updates it if it exists.
     */
    private void addWallForce(String name, Force force) {
    	wallForces.add(force);
    	wallForcesMap.put(name, wallForces);
    }
    
    /**
     * Gets a vector from the vector map.
     */
    public Vector getVector(String name) {
    	if (myVectors.containsKey(name)) {
    		return myVectors.get(name);
    	} else {
    		return null;
    	}
    }
    
    /**
     * Gets the wall vectors from the wall vector map.
     */
    public List<Vector> getWallVectors(String name) {
    	if (wallVectorsMap.containsKey(name)) {
    		return wallVectorsMap.get(name);
    	} else {
    		return null;
    	}
    }
    
    
    /**
     * Gets a force from the force map.
     */
    public Force getForce(String name) {
    	if (myForces.containsKey(name)) {
    		return myForces.get(name);
    	} else {
    		return null;
    	}
    }
    
    /**
     * Gets a list of wall forces from the force map.
     */
    public List<Force> getWallForces(String name) {
    	if (wallForcesMap.containsKey(name)) {
    		return wallForcesMap.get(name);
    	} else {
    		return null;
    	}
    }
}
