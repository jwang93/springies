package simulation;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Interface to be extended by all objects in the simulation.
 * @author james
 *
 */
public interface SimulationObject {

    /**
     * Updates the object as needed for the update cycle.
     * @param elapsedTime is time since last update
     * @param bounds is size of view
     * @param lastKeyPressed is last key pressed by user
     */
    public void update (double elapsedTime, Dimension bounds, int lastKeyPressed);

    /**
     * Paints the object on the canvas.
     * @param pen is the Graphics2D object used to paint on the canvas
     */
    public void paint (Graphics2D pen);

    /**
     * Prepares the object for the next update cycle.
     * @param elapsedTime is time since last update
     * @param bounds is size of view
     */
    public void updateEnd (double elapsedTime, Dimension bounds);

}
