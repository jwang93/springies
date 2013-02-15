package simulation;

import java.awt.Dimension;
import java.awt.Point;
import util.Location;
import util.Pixmap;
import util.Vector;


public class InvisibleMass extends Mass {

    public static final Pixmap DEFAULT_IMAGE = new Pixmap("empty.gif");
    public static final Dimension DEFAULT_SIZE = new Dimension(0, 0);

    public InvisibleMass (double x, double y, double mass, Model model) {
        super(x, y, mass, model);
    }

    @Override
    public void update (double elapsedTime, Dimension bounds) {
        // do nothing because nothing affects me
    }

    @Override
    public void applyForce (Vector force) {
        // do nothing because nothing affects me
    }

    public void changeLocation (Point lastMousePosition) {
        if (lastMousePosition != null) {
            Location newLocation = new Location(lastMousePosition);
            if (newLocation != null) {
                this.setCenter(newLocation);
            }
        }
    }
}
