package simulation;

import java.awt.event.KeyEvent;

public class Keywords {

	public static final String GRAVITY_KEYWORD = "gravity";
	public static final String VISCOSITY_KEYWORD = "viscosity";
	public static final String CENTER_OF_MASS_KEYWORD = "centermass";
	public static final String WALL_KEYWORD = "wall";
	
	public static final String WALL_1 = "wall1";
	public static final String WALL_2 = "wall2";
	public static final String WALL_3 = "wall3";
	public static final String WALL_4 = "wall4";
	
	
	public static final String SPRING_KEYWORD = "spring";
	public static final String MUSCLE_KEYWORD = "muscle";
	public static final String MASS_KEYWORD = "mass";
	
	
	public static final int CANVAS_WIDTH = 800;
	public static final int CANVAS_HEIGHT = 600;
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int DEGREES_TURN_AROUND = 180;

    public static final int TOGGLE_GRAVITY = KeyEvent.VK_G;
    public static final int TOGGLE_VISCOSITY = KeyEvent.VK_V;
    public static final int TOGGLE_CENTER_OF_MASS = KeyEvent.VK_C;
    public static final int TOGGLE_WALL_1 = KeyEvent.VK_1;
    public static final int TOGGLE_WALL_2 = KeyEvent.VK_2;
    public static final int TOGGLE_WALL_3 = KeyEvent.VK_3;
    public static final int TOGGLE_WALL_4 = KeyEvent.VK_4;
    public static final int INCREASE_BOUNDS = KeyEvent.VK_UP;
    public static final int DECREASE_BOUNDS = KeyEvent.VK_DOWN;

    public static final int ASCII_VALUE_ZERO = 48;


    public static final double DEFAULT_VISCOSITY_SCALE = 1.5;
    public static final double DEFAULT_GRAVITY_MAGNITUDE = 1.5;
    public static final double DEFAULT_GRAVITY_ANGLE = 90.0;
    public static final double DEFAULT_CENTER_OF_MASS_MAGNITUDE = 0.5;
    public static final double DEFAULT_CENTER_OF_MASS_EXPONENT = 1.0;
    public static final double DEFAULT_WALL_REPULSION_MAGNITUDE = 20.0;
    public static final double DEFAULT_WALL_REPULSION_EXPONENT = 0.7;
	public static final double PIXEL_ADJUSTMENT = 10.0;





}
