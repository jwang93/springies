package simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import view.Canvas;


/**
 * XXX
 * 
 * @author Robert C. Duvall
 */
public class Factory {

    // mass IDs
    private Map<Integer, Mass> myMasses = new HashMap<Integer, Mass>();
    private Canvas canvas;

    public Factory (Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * XXX.
     */
    public void loadModel (Model model, File modelFile) {
        model.setEnvironment(loadEnvironment());
        try {
            Scanner input = new Scanner(modelFile);
            while (input.hasNext()) {
                Scanner line = new Scanner(input.nextLine());
                if (line.hasNext()) {
                    String type = line.next();
                    if (Keywords.MASS_KEYWORD.equals(type)) {
                        model.add(massCommand(line, model));
                    }
                    else if (Keywords.SPRING_KEYWORD.equals(type)) {
                        model.add(springCommand(line));
                    }
                    else if (Keywords.MUSCLE_KEYWORD.equals(type)) {
                        model.add(muscleCommand(line));
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

    // create the environment from formatted data
    private Environment loadEnvironment () {
        return new Environment();
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
            myMasses.put(id, result);
        }

        else {
            result = new Mass(x, y, mass, model);
            myMasses.put(id, result);
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

    // create muscle from formatted data
    private Muscle muscleCommand (Scanner line) {
        Mass m1 = myMasses.get(line.nextInt());
        Mass m2 = myMasses.get(line.nextInt());
        double restLength = line.nextDouble();
        double ks = line.nextDouble();
        double amp = line.nextDouble();
        return new Muscle(m1, m2, restLength, ks, amp);
    }

}
