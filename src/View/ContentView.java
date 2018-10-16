package View;

import View.Content.ContentPanel;
import View.Content.MapContext.MapView;
import View.Content.MapContext.SwingView;
import View.Content.WindowBar;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * This class has the responsibility of displaying all content
 * of the program and scaling it accordingly to the window size.
 */
public class ContentView extends Pane {
    private MapView mapCanvas;
    private static SwingView swingView;

    /**
     * Construct the contentView.
     *
     * @param stage  Uses the stage to create the WindowBar.
     * @param width  Uses the width to create the MapView.
     * @param height Uses the height to create the MapView.
     */
    ContentView(Stage stage, double width, double height) {
        mapCanvas = new MapView(width, height);
        WindowBar windowBar;

        windowBar = new WindowBar(stage, swingView);
        BorderPane borderPane = new BorderPane();

        borderPane.setLeft(mapCanvas);
        borderPane.setTop(windowBar);
        borderPane.setRight(new ContentPanel());

        this.getChildren().addAll(borderPane);
    }



    /**
     * Get the MapView.
     * @return The mapCanvas.
     */
    public MapView getMapView() {
        return mapCanvas;
    }

    public static void setSwingView(SwingView swingView) {
        ContentView.swingView = swingView;
    }

    public static SwingView getSwingView() {
        return swingView;
    }
}
