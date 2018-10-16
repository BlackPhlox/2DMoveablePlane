package View.Content.Tabs;

import Model.Model;
import Model.PointOfInterest;
import View.Content.ContentPanel;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

/**
 * Makes the point of interest tab.
 * Extends the Tab class.
 */
public class PoiTab extends Tab {
    private static ListView<PointOfInterest> uiPois;

    /**
     * Names the tab POI.
     * Adds functionality.
     */
    public PoiTab(){
        super("POI");
        setClosable(false);
        uiPois = new ListView<>();
        VBox content = new VBox(uiPois);
        content.setPadding(new Insets(5,40,5,5));
        setContent(content);
        uiPois.prefHeightProperty().bind(content.heightProperty().subtract(50));
        //Go to this tab if an PointOfInterest has been added to the ListView
        uiPois.getItems().addListener((ListChangeListener<? super PointOfInterest>) e-> ContentPanel.setTab(this));
    }

    /**
     * Get the Point of interest for the user interface.
     * @return Gives the Point of interest for the user interface.
     */
    public static ListView<PointOfInterest> getUiPois() {
        return uiPois;
    }

    /**
     * Loads point of interest.
     */
    public static void loadPois(){
        if (uiPois != null){
            for(PointOfInterest poi : Model.getInstance().getMapPois()){
                uiPois.getItems().add(poi);
                poi.update();
                poi.maxWidthProperty().bind(uiPois.widthProperty());
            }
        }
    }
}
