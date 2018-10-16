package Model;

import Controller.UIAction.WindowAction.AlertTemplate;
import Model.OSM.OSMWayType;
import View.Content.MapContext.MapView;
import View.WindowView;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.TextAlignment;
import java.awt.geom.Point2D;
import java.io.Serializable;

import static View.WindowView.debugPrintln;

/**
 * This class handles the point of interest part of the code.
 */
public class PointOfInterest extends Button implements Serializable {
    private double x,y;
    private TextFieldSerial name = new TextFieldSerial();
    private String poiName;


    /**
     * Creates a new PoI for displaying on the map.
     * @param x Longitude.
     * @param y Latitude.
     */
    public PointOfInterest(double x, double y){
        this.x = x;
        this.y = y;
        String buildingName = Model.getInstance().getNearestName(OSMWayType.BUILDING,new Point2D.Double(x,y));
        if(buildingName != null) poiName = buildingName; else poiName = this.toString();
        update();
    }

    /**
     * Updates a PoI and enables the deletion of it.
     */
    public void update(){
        setText(poiName);
        Button remove_btn = new Button("X");
        remove_btn.setStyle("-fx-background-color: rgba(0,0,0,0)");
        remove_btn.setOnAction(e ->{
            debugPrintln("Removed " + this);
            MapView.getSwingView().repaint();
            Model.getInstance().removePOI(this);
        });
        setGraphic(remove_btn);
        setContentDisplay(ContentDisplay.RIGHT);
        setAlignment(Pos.BASELINE_RIGHT);
        setTextAlignment(TextAlignment.LEFT);

        setOnMousePressed(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY))MapView.getSwingView().setAbsPosition(x,y); else {
                name.setText(getText());
                setText("");
                setGraphic(name);
            }
        });

        name.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        name.setOnAction(event -> {
            if(name.getText().matches(".+")){
                setText(name.getText());
                poiName = name.getText();
                setGraphic(remove_btn);
            } else AlertTemplate.infoAlert("Input Error", "Invalid name of a Point of Interest", "Sadly \"" + name.getText() + "\" is not a valid name");
        });
    }

    /**
     * Sets the name of a PoI
     * @param name Uses the name to set the name.
     */
    public void setName(String name) {
        this.name.setText(name);
    }

    /**
     * Returns the longitude.
     * @return X.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the latitude.
     * @return Y.
     */
    public double getY() {
        return y;
    }

    /**
     * ToString method for displaying the object as text.
     * @return Object as text.
     */
    @Override
    public String toString() {
        if(name.getText().equals("")) return "Lon: " + (Model.getLonFactor()*x) + " Lat: " + (-y);
        else return name.getText();
    }
}
