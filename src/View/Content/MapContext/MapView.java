package View.Content.MapContext;

import Model.Model;
import View.Content.Components.ZoomSlider;
import View.Content.ContentPanel;
import View.ContentView;
import View.WindowView;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javax.swing.*;
import java.awt.*;

/**
 * This is a wrapper class for the swing canvas that allows for drawing with swing.
 */
public class MapView extends Pane {
    private static SwingView swingView;
    private static SwingNode swingNode;
    private static BorderPane borderPane;

    /**
     * The constructor creates the MapView, with style, width, heigh and content.
     * @param width Uses the width to create the MapView's width.
     * @param height Uses the height to create the MapView's height.
     */
    public MapView(double width, double height){
        this.setStyle("-fx-background-color: #000");
        swingNode = new SwingNode();
        swingView = new SwingView();
        ContentView.setSwingView(swingView);
        Model model = Model.getInstance();
        resetView();
        swingView.setPreferredSize(
                new Dimension(
                        (int)(width - ContentPanel.getRightPanelWidth()+20),
                        (int)(height - 25)
                )
        );

        createSwingContent();
        this.getChildren().add(swingNode);
        swingNode.toBack();

        ZoomSlider slider = ZoomSlider.getInstance();
        borderPane = new BorderPane();
        borderPane.setRight(slider);
        borderPane.setPadding(new Insets(5));
        for(int i = 0; i < borderPane.getChildren().size(); i++){
            turnOffPickOnBoundsFor(borderPane.getChildren().get(i),true);
        }
        turnOffPickOnBoundsFor(borderPane, true);
        this.getChildren().add(borderPane);

        new MapContextMenu(swingView,swingNode);
        swingView.repaint();
    }

    public static void resetView(){
        int width = WindowView.getWidth();
        swingView.setAbsPosition();
        swingView.zoomPan(-Model.getInstance().getMinLon(), -Model.getInstance().getMaxLat());
        swingView.zoom(width / (Model.getInstance().getMaxLon() - Model.getInstance().getMinLon()), 0, 0);
    }

    /**
     * Creates the Swing content.
     */
    private static void createSwingContent() {
        Platform.runLater(() -> {
            swingNode.setContent(swingView);
        });
    }

    /**
     * Get the SwingView.
     * @return Gives the swingView.
     */
    public static SwingView getSwingView() {
        return swingView;
    }

    /**
     * Resize's the view.
     * @param width Uses the width to set the preferred size.
     * @param height Uses the height to set the preferred size.
     */
    public static void resizeView(int width, int height){
        swingView.setPreferredSize(new Dimension((int)Math.round(width-ContentPanel.getRightPanelWidth()+20), height-25));
        swingNode.setContent(swingView);
        borderPane.setPrefSize(Math.round(width-ContentPanel.getRightPanelWidth()+20), height-63);
    }

    /**
     * Get the swing node.
     * @return The swingNode.
     */
    public static SwingNode getSwingNode() {
        return swingNode;
    }

    /**
     * Turns off pick on bounds.
     * So that the UI can be put on top of swingNode.
     *
     * user1638436 2013
     * https://goo.gl/AAsuPo
     * @param n Uses a node to change pick on bounds for the parent node and its children.
     * @param plotContent Uses the boolean to know when to turn off pick on bounds.
     * @return Gives the boolean result which is true if !plotContent.
     */
    private boolean turnOffPickOnBoundsFor(Node n, boolean plotContent) {
        boolean result = false;
        boolean plotContentFound = false;
        n.setPickOnBounds(false);
        if(!plotContent){
            if(containsStyle(n)){
                plotContentFound = true;
                result = true;
            }
            if (n instanceof Parent) {
                for (Node c : ((Parent) n).getChildrenUnmodifiable()) {
                    if(turnOffPickOnBoundsFor(c, plotContentFound)){
                        result = true;
                    }
                }
            }
            n.setMouseTransparent(!result);
        }
        return result;
    }

    /**
     * Checks if the node contains a style.
     *
     * user1638436 2013
     * https://goo.gl/AAsuPo
     * @param node Uses the node to check if it contain a style.
     * @return Whether or not the node contains a style.
     */
    private boolean containsStyle(Node node){
        boolean result = false;
        for (String object : node.getStyleClass()) {
            if(object.equals("plot-content")){
                result = true;
                break;
            }
        }
        return result;
    }
}
