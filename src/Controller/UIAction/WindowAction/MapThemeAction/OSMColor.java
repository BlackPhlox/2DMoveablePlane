package Controller.UIAction.WindowAction.MapThemeAction;

import Controller.UIAction.StringManipulation;
import Model.OSM.OSMWayType;
import View.Content.MapContext.MapView;
import View.WindowView;
import javafx.geometry.Pos;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import static View.WindowView.debugPrintln;

/**
 * Makes it possible to personalize which color to be used with RGB color.
 */
public class OSMColor extends HBox {
    private OSMWayType osmType;
    private java.awt.Color color;

    /**
     * Initializes the OSMWayTypes.
     * @param osmWayType Uses OSMWayTypes as parameter to know the amount of OSMWayTypes.
     */
    OSMColor (OSMWayType osmWayType){
        this(osmWayType,null, false);
    }

    /**
     * Creates the color, and repaints the SwingView with the color.
     * @param osmWayType Takes a specific osmWayType.
     * @param c Uses the java library awt to set the color.
     * @param mutable Defines if it is mutable or not.
     */
    OSMColor(OSMWayType osmWayType, java.awt.Color c, boolean mutable){
        this.osmType = osmWayType;
        this.color = c;
        if(color == null){
            color = osmType.getColor();
        }
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        ColorPicker cp = new ColorPicker(Color.rgb(r,g,b));
        if(mutable){
            cp.setOnAction(e2 -> {
                Color currentColor = cp.getValue();
                int newR = (int)Math.floor(currentColor.getRed()*255);
                int newG = (int)Math.floor(currentColor.getGreen()*255);
                int newB = (int)Math.floor(currentColor.getBlue()*255);
                debugPrintln("Selected color: "+newR + " " + newG + " " + newB);
                osmType.setColor(new java.awt.Color(newR,newG,newB));
                MapView.getSwingView().repaint();
            });
        } else {
            cp.setDisable(true);
            cp.setStyle("-fx-background-color: #c2c2c2");
        }
        getChildren().addAll(new Label(StringManipulation.toProperCase(osmType.name())),cp);
        setAlignment(Pos.CENTER_RIGHT);
        setSpacing(5);
    }

    /**
     * Updates if the color is not equal null.
     */
    public void update(){
        if(color != null){
            osmType.setColor(color);
        }
    }

    /**
     * Gives the osmType.
     * @return The OSMWayType.
     */
    public OSMWayType getOsmType() {
        return osmType;
    }
}
