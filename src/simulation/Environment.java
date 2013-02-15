package simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.Vector;


public class Environment {

    private static final ArrayList<Force> myForces = new ArrayList<Force>();

    private static final String DEFAULT_ENV_FILE = "src/data/environment.xsp";

    public Environment () {
        File file = new File(DEFAULT_ENV_FILE);
        loadEnvironment(file);
    }

    /**
     * Load the environment and its forces.
     */
    public void loadEnvironment (File file) {
        loadDefaults();
        try {
            Scanner input = new Scanner(file);
            while (input.hasNext()) {
                Scanner line = new Scanner(input.nextLine());
                if (line.hasNext()) {
                    String type = line.next();
                    if (Keywords.GRAVITY_KEYWORD.equals(type)) {
                        removeForce(new ForceGravity());
                        addForce(gravityCommand(line));
                    }
                    else if (Keywords.VISCOSITY_KEYWORD.equals(type)) {
                        removeForce(new ForceViscosity());
                        addForce(viscosityCommand(line));
                    }
                    else if (Keywords.CENTER_OF_MASS_KEYWORD.equals(type)) {
                        removeForce(new ForceCenterMass());
                        addForce(centerMassCommand(line));
                    }
                    else if (Keywords.WALL_KEYWORD.equals(type)) {
                        Force wallForce = wallCommand(line);
                        removeForce(wallForce);
                        addForce(wallForce);
                    }
                }
            }
            input.close();
            System.out.println(myForces);
        }
        catch (FileNotFoundException e) {
            System.err.printf("environment.xsp file read error, please check %s", DEFAULT_ENV_FILE);
            e.printStackTrace();
        }
    }

    /**
     * Adds default untoggled forces if not specified in environment.
     */
    private void loadDefaults () {
        addForce(new ForceGravity());
        addForce(new ForceViscosity());
        addForce(new ForceCenterMass());
        addForce(new ForceWallRepulsion(Keywords.WALL_ID_TOP));
        addForce(new ForceWallRepulsion(Keywords.WALL_ID_RIGHT));
        addForce(new ForceWallRepulsion(Keywords.WALL_ID_BOTTOM));
        addForce(new ForceWallRepulsion(Keywords.WALL_ID_LEFT));
    }

    /**
     * Adds a gravity force.
     */
    private Force gravityCommand (Scanner line) {
        Force gravity = new ForceGravity(line);
        return gravity;
    }

    /**
     * Adds a viscosity force.
     */
    private Force viscosityCommand (Scanner line) {
        Force viscosity = new ForceViscosity(line);
        return viscosity;
    }

    /**
     * Adds a center of mass force.
     */
    private Force centerMassCommand (Scanner line) {
        Force centerMass = new ForceCenterMass(line);
        return centerMass;
    }

    /**
     * Adds a wall force.
     */
    private Force wallCommand (Scanner line) {
        Force wallRepulsion = new ForceWallRepulsion(line);
        return wallRepulsion;
    }

    /**
     * Adds a force to the list.
     */
    private void addForce (Force force) {
        myForces.add(force);
    }

    /**
     * Removes a force from the list.
     */
    private void removeForce (Force force) {
        for (int i = 0; i < myForces.size(); i++) {
            Force other = myForces.get(i);
            if (force.equals(other)) {
                myForces.remove(i);
                i--;
            }
        }
    }

    /**
     * Searches list of forces.
     */
    public Force findForce (Force search) {
        for (Force force : myForces) {
            if (force.equals(search)) return force;
        }
        return null;
    }

    /**
     * Returns the total sum of all environment forces on a mass.
     */
    public Vector getTotalForce (Mass m) {
        Vector result = new Vector();
        for (Force force : myForces) {
            result.sum(force.getForceOnMass(m));
        }
        return result;
    }

    /**
     * Returns the list of all environment forces.
     */
    public List<Force> getForces () {
        return myForces;
    }

}
