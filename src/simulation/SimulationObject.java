package simulation;

import java.awt.Dimension;
import java.awt.Graphics2D;

public interface SimulationObject {

	/**
	 * Updates the object as needed for the update cycle.
	 */
	public void update(double elapsedTime, Dimension bounds);
	
	/**
	 * Paints the object on the canvas.
	 */
	public void paint(Graphics2D pen);
	
	/**
	 * Prepares the object for the next update cycle.
	 */
	public void updateEnd(double elapsedTime, Dimension bounds);

}
