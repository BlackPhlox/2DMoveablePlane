package View.Content.MapContext;

import Controller.InputAction.MouseController;
import Model.OSM.OSMAddress;
import Model.PointOfInterest;
import Model.Model;
import View.Content.Tabs.RouteTab;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import Model.PathType;

/**
 * When you click on the SwingNode, you request the menu for the map.
 */
class MapContextMenu extends ContextMenu{
    private static String nearestName;

    /**
     * The constructor of the class, adds the listener and prepares the menu items.
     * @param swingView Uses swingView to get the mouse position and repaint the swingView.
     * @param swingNode Uses swingNode to show the menu, and close it.
     */
    MapContextMenu(SwingView swingView, SwingNode swingNode) {
        MenuItem nearest = new MenuItem();
        nearest.setStyle("-fx-opacity: 1.0; -fx-underline: true;");
        nearest.setDisable(true);
        focusedProperty().addListener(e ->{
            if(nearestName == null){
                nearest.setVisible(false);
            } else {
                if(!nearest.isVisible()) nearest.setVisible(true);
                nearest.setText(nearestName);
            }
        });
        MenuItem addFrom = new MenuItem("Set From");
        addFrom.setOnAction(e-> addToModel(PathType.START));

        MenuItem addBreakPoint = new MenuItem("Add Breakpoint");
        addBreakPoint.setOnAction(e-> addToModel(PathType.BREAKPOINT));

        MenuItem addTo = new MenuItem("Set To");
        addTo.setOnAction(e-> addToModel(PathType.END));

        MenuItem addPOI = new MenuItem("Add POI");
        addPOI.setOnAction(e->{
            Point2D pos = swingView.toModelCoords(MouseController.getLastMousePosition());
            Model.getInstance().addPOI(new PointOfInterest(pos.getX(),pos.getY()));
            swingView.repaint();
        });

        getItems().addAll(nearest,addFrom,addBreakPoint,addTo,addPOI);

        swingNode.setOnContextMenuRequested(e-> show(swingNode,e.getScreenX(),e.getScreenY()));

        swingNode.setOnMousePressed(e->hide());
    }

    /**
     * Set the nearest name.
     * @param nearestName Uses a String to set as the nearest name.
     */
    static void setNearestName(String nearestName) {
        MapContextMenu.nearestName = nearestName;
    }

    /**
     * Adds the selected point to the model, and makes the action which is required by the pathType.
     * @param pathType Gives pathType as parameter, so that it knows which case to run.
     */
    private void addToModel(PathType pathType){
        double lon = MapView.getSwingView().toModelCoords(MouseController.getLastMousePosition()).getX();
        double lat = MapView.getSwingView().toModelCoords(MouseController.getLastMousePosition()).getY();

        OSMAddress closestAddress = Model.getInstance().getNearestAddress(lon, lat);
        Shape point = new Ellipse2D.Double(closestAddress.getLon(), closestAddress.getLat(), 0, 0);
        Model.getInstance().getSelectedAddresses().add(point);
        SwingView.getMarkingMap().put(pathType,closestAddress);

        switch (pathType){
            case START:
                RouteTab.getSearchFieldFrom().setSelectedAddress(closestAddress);
                break;
            case BREAKPOINT:
                RouteTab.pressAddButton(RouteTab.getSearchFields());
                RouteTab.getSearchFieldBreakpoint().setSelectedAddress(closestAddress);
                break;
            case END:
                RouteTab.getSearchFieldTo().setSelectedAddress(closestAddress);
                break;
        }
    }
}
