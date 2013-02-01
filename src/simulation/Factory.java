package simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * XXX
 * 
 * @author Robert C. Duvall
 */
public class Factory {
    // data file keywords
    private static final String MASS_KEYWORD = "mass";
    private static final String SPRING_KEYWORD = "spring";
    private static final String GRAVITY_KEYWORD = "gravity";


    // mass IDs
    Map<Integer, Mass> myMasses = new HashMap<Integer, Mass>();


    /**
     * XXX.
     */
    public void loadModel (Model model, File modelFile) {
        try {
            Scanner input = new Scanner(modelFile);
            while (input.hasNext()) {
                Scanner line = new Scanner(input.nextLine());
                if (line.hasNext()) {
                    String type = line.next();
                    if (MASS_KEYWORD.equals(type)) {
                        model.add(massCommand(line, model));
                    }
                    else if (SPRING_KEYWORD.equals(type)) {
                        model.add(springCommand(line));
                    } 
                    else if (GRAVITY_KEYWORD.equals(type)) {
                        model.add(gravityCommand(line));
                    } 
                }
            }
            input.close();
        }
        catch (FileNotFoundException e) {
            // should not happen because File came from user selection
            e.printStackTrace();
        }
    }

    // create mass from formatted data
    private Mass massCommand (Scanner line, Model model) {
        int id = line.nextInt();
        double x = line.nextDouble();
        double y = line.nextDouble();
        
        double mass = line.nextDouble();
        
        Mass result = null;
        
        if (mass < 0.0) {
        	result = new FixedMass(x, y, mass, model);
        	System.out.println("Added a fixed mass");
            myMasses.put(id,  result);
        }
        
        else {
        	result = new Mass(x, y, mass, model);
            myMasses.put(id,  result);
        }
        return result;
    }

    // create spring from formatted data
    private Spring springCommand (Scanner line) {
        Mass m1 = myMasses.get(line.nextInt());
        Mass m2 = myMasses.get(line.nextInt());
        double restLength = line.nextDouble();
        double ks = line.nextDouble();
        return new Spring(m1, m2, restLength, ks);
    }
    
    
    private Gravity gravityCommand (Scanner line) {
        double myDirection = line.nextDouble();
        double myMagnitude = line.nextDouble();
        return new Gravity(myDirection, myMagnitude);
    }
    
}
