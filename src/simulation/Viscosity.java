package simulation;

public class Viscosity {
	
	private double scale;
	
	// IMPT: VISCOSITY IS NOT A VECTOR, IT'S JUST A GLOBAL CONSTANT k
	public Viscosity (double scale) {
		this.scale = scale;
	}
	
	public double getScale() {
		return scale;
	}
}
