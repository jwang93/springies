package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * This class represents text that appears on the screen and
 * adds some utility functions to the Image class.
 * 
 * @author Robert C. Duvall
 */
public class Text {
    // possible font styles
    public static final String FONT_SERIF = "Serif";
    public static final String FONT_SANSSERIF = "SansSerif";
    public static final String FONT_MONOSPACED = "Monospaced";
    // underlying implementation
    private String myText;
    private Font myFont;


    /**
     * Construct a shape at the given position, with the given velocity,
     * size, and color, displaying the given text string.
     */
    public Text (String text) {
        myText = text;
        myFont = new Font("Default", Font.BOLD, 14);
    }

    /**
     * Returns text of this shape
     */
    public String getText () {
        return myText;
    }

    /**
     * Change text of this shape
     */
    public void setText (String s) {
        myText = s;
    }

    /**
     * Change the font of this shape's text.
     */
    public void setFont (String type, int size) {
        myFont = new Font(type, Font.BOLD, size);
    }

    /**
     * Describes how to draw the shape on the screen.
     */
    public void paint (Graphics2D pen, Point2D center, Color color) {
        Color oldColor = pen.getColor();
        // set attributes
        pen.setColor(color);
        pen.setFont(myFont);
        // get text size
        TextLayout layout = new TextLayout(myText, myFont, pen.getFontRenderContext());
        float height = layout.getAscent() + layout.getDescent();
        Rectangle2D bounds = layout.getBounds();
        bounds.setRect(-bounds.getWidth() / 2, -height / 2, bounds.getWidth(), bounds.getHeight());
        // draw text
        layout.draw((Graphics2D) pen, (float) (center.getX() - bounds.getWidth() / 2),
                    (float) (center.getY() + bounds.getHeight() / 2 - layout.getDescent()));
        // unset attributes
        pen.setColor(oldColor);
    }
}
