package simulation;

public class CenterOfMass {

	private double myMagnitude;
	private double myExpVal;
	
	public CenterOfMass (double magnitude, double exp_value) {
		myMagnitude = magnitude;
		myExpVal = exp_value;
	}
	
	public double getCOM_Mag() {
		return myMagnitude;
	}
	
	public double getCOM_ExpVal() {
		return myExpVal;
	}
}
