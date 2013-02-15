package simulation;

import java.util.HashMap;
import util.Vector;


/**
 * A bean class that holds properties of forces.
 * 
 * @author james
 * 
 */
public abstract class Force {

    private HashMap<String, Double> myProperties;
    private String myName;
    private boolean myToggled;
    private Vector myDefaultVector;

    public Force () {
        myProperties = new HashMap<String, Double>();
        myToggled = false;
        myDefaultVector = new Vector();
    }

    /**
     * Adds a new property to the force, or updates its value if it exists.
     */
    public void addProperty (String name, Double value) {
        myProperties.put(name, value);
    }

    /**
     * Gets a property from the force.
     */
    public Double getProperty (String name) {
        if (myProperties.containsKey(name))
            return myProperties.get(name);
        else return null;
    }

    /**
     * Checks if a property exists for this force.
     */
    public boolean checkProperty (String name) {
        return myProperties.containsKey(name);
    }

    /**
     * Sets the name of the force.
     */
    public void setName (String name) {
        myName = name;
    }

    /**
     * Returns the name of the force.
     */
    public String getName () {
        return myName;
    }

    /**
     * Sets the toggled state of the force.
     */
    public void setToggle (boolean toggle) {
        myToggled = toggle;
    }

    /**
     * Returns the toggled state of the force.
     */
    public boolean getToggle () {
        return myToggled;
    }

    /**
     * Sets the default vector for the force.
     */
    public void setDefaultVector () {
        Vector vector = new Vector();
        if (getProperty(Keywords.MAGNITUDE) != null) {
            vector.setMagnitude(getProperty(Keywords.MAGNITUDE));
        }
        else {
            vector.setMagnitude(0);
        }
        if (getProperty(Keywords.DIRECTION) != null) {
            vector.setAngle(getProperty(Keywords.DIRECTION));
        }
        else {
            vector.setAngle(0);
        }
        myDefaultVector = vector;
    }

    /**
     * Overwrites equals such that forces with the same name are considered equal.
     */
    public boolean equals (Force other) {
        return myName.equals(other.getName());
    }

    /**
     * Overwrites hashcode such that forces with the same name have the same hashcode.
     */
    @Override
    public int hashCode () {
        return myName.hashCode();
    }

    /**
     * Returns a string representation of this force.
     */
    @Override
    public String toString () {
        StringBuilder result = new StringBuilder();
        result.append(myName);
        result.append(": ");
        for (String key : myProperties.keySet()) {
            result.append(key);
            result.append("=");
            result.append(myProperties.get(key));
            result.append(" ");
        }
        return result.toString();
    }

    /**
     * Returns the default vector for the force.
     */
    public Vector getDefaultVector () {
        return myDefaultVector;
    }

    /**
     * Returns the vector for this force specific to a mass.
     */
    public abstract Vector getForceOnMass (Mass m);
}
