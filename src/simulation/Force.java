package simulation;

import java.util.HashMap;

/**
 * A bean class that holds properties of forces.
 * @author james
 *
 */
public class Force {

	private HashMap<String,Double> myProperties;
	private String myName;
	
	public Force(String name) {
		myProperties = new HashMap<String,Double>();
		myName = name;
	}
	
	/**
	 * Adds a new property to the force, or updates its value if it exists. 
	 */
	public void addProperty(String name, Double value) {
		myProperties.put(name, value);
	}
	
	/**
	 * Gets a property from the force.
	 */
	public Double getProperty(String name) {
		if (myProperties.containsKey(name)) {
			return myProperties.get(name);
		} else {
			return null;
		}
	}
	
	/**
	 * Checks if a property exists for this force.
	 */
	public boolean checkProperty(String name) {
		return myProperties.containsKey(name);
	}
	
	/**
	 * Gets the name of the force.
	 */
	public String getName() {
		return myName;
	}
}
