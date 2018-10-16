package Model.OSM;
import java.awt.*;
import static View.Content.MapContext.SwingView.getLOD;

/**
 * Class for handling the colors of the different road types
 */
public enum OSMWayType {
    BACKGROUND      (new Color(74, 137, 243),   0),
    COASTLINE       (new Color(237,237,237),0),
    WATER           (BACKGROUND.getColor(),                  getLOD()[5], 0.045 , getLOD()[2]),
    LANDUSE         (new Color(182, 182, 182),     getLOD()[2], 0.03  , getLOD()[2]),
    PARK            (new Color(193, 237, 173),     getLOD()[2], 0.03  , getLOD()[2]),
    UNKNOWN         (Color.BLACK,                            getLOD()[0], new BasicStroke(0.00001f)),
    ROAD            (new Color(0, 0, 0),           getLOD()[1], new BasicStroke(0.00001f)),
    FERRY           (new Color(0, 14, 255),        getLOD()[1], new BasicStroke(0.00001f)),
    BIKEPATH        (new Color(0, 128, 191),       getLOD()[0], new BasicStroke(0.00001f)),
    FOOTWAY         (new Color(247, 155, 219),     getLOD()[0], new BasicStroke(0.00001f)),
    HIGHWAY         (new Color(255, 190, 91),      getLOD()[5], new BasicStroke(0.0001f)) ,
    RAILWAY         (new Color(254, 218, 168),     getLOD()[3], new BasicStroke(0.00003f)),
    SUBWAY          (new Color(112, 66, 40),       getLOD()[1], new BasicStroke(0.00003f)),
    BUILDING        (new Color(172, 169, 151),     getLOD()[0]),
    HEALTHCARE      (new Color(255,0,0, 100),   getLOD()[0]),
    EDUCATION       (new Color(0, 255, 0, 100), getLOD()[0]),
    FINANCIAL       (new Color(0,0,255,100),    getLOD()[0]),
    SERVICES        (new Color(255,140,0,100),  getLOD()[0]),
    TRANSPORTATION  (new Color(135,206,250,100),getLOD()[0]),
    BEACH           (new Color(255,255,224),       getLOD()[0]),
    HEATH           (new Color(222,184,135,100),getLOD()[0]),
    PATH            (new Color(188, 12, 25),       0,           new BasicStroke(0.000055f));

    private Color color;
    private int drawLevel;
    private int lowerDrawLevel;
    private Stroke stroke;
    private double sizeDrawLevel = 0;

    /**
     * Constructor.
     * @param c Color.
     * @param drawLevel The draw level.
     */
    OSMWayType(Color c, int drawLevel){
        this(c,drawLevel,null,0,0);
    }

    /**
     * Constructor.
     * @param c Color.
     * @param drawLevel The draw level.
     * @param stroke The stroke.
     */
    OSMWayType(Color c, int drawLevel, Stroke stroke){
        this(c,drawLevel,stroke,0,0);
    }

    OSMWayType(Color c, int drawLevel, double sizeDrawLevel, int lowerDrawLevel){this(c,drawLevel,null,sizeDrawLevel,lowerDrawLevel);}

    /**
     * Constructor.
     * @param c Color.
     * @param drawLevel The draw level.
     * @param sizeDrawLevel The size of the draw level.
     */
    OSMWayType(Color c, int drawLevel, double sizeDrawLevel){
        this(c,drawLevel,null,sizeDrawLevel,0);
    }

    /**
     * Constructor.
     * @param c Color.
     * @param drawLevel The draw level.
     * @param stroke The stroke.
     * @param sizeDrawLevel The size of the draw level.
     */
    OSMWayType(Color c, int drawLevel, Stroke stroke, double sizeDrawLevel, int lowerDrawLevel){
        this.color = c;
        this.drawLevel = drawLevel;
        this.stroke = stroke;
        this.sizeDrawLevel = sizeDrawLevel;
        this.lowerDrawLevel = lowerDrawLevel;
    }

    /**
     * Returns the stroke.
     * @return Stroke.
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * Returns the sizeDrawLevel.
     * @return SizeDrawLevel.
     */
    public double getSizeDrawLevel() {
        return sizeDrawLevel;
    }

    /**
     * Returns drawLevel.
     * @return DrawLevel.
     */
    public int getDrawLevel() {
        return drawLevel;
    }

    /**
     * Returns color.
     * @return Color.
     */
    public Color getColor(){
        return color;
    }

    /**
     * Sets the color.
     * @param c Color to set.
     */
    public void setColor(Color c){
        color = c;
    }

    public int getLowerDrawLevel() {
        return lowerDrawLevel;
    }
}
