package View.Content;

import View.Content.Tabs.FindTab;
import View.Content.Tabs.PoiTab;
import View.Content.Tabs.RouteTab;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

/**
 * This class represents the panel that allows for
 * a variety of different functionality related to
 * roam and navigate around the map shown in the canvas.
 */
public class ContentPanel extends StackPane {
    private static FindTab addressTab;
    private static Double rightPanelWidth = 350.0;
    private static RouteTab routeTab = new RouteTab();
    private static PoiTab poiTab = new PoiTab();
    private static HBox tabContainer;

    /**
     * Constructor for ContentPanel
     */
    public ContentPanel(){
        addressTab = new FindTab();
        tabContainer = new HBox(new ContentPanelTab());
        this.setPrefWidth(rightPanelWidth);
        this.getChildren().addAll(tabContainer);
    }

    /**
     * Nested class ContentPanelTab
     */
    public class ContentPanelTab extends TabPane{
        ContentPanelTab() {
            this.setPrefWidth(rightPanelWidth);
            this.getTabs().addAll(addressTab, routeTab, poiTab);
        }
    }

    /**
     * Set the tab.
     * @param tab Uses a tab to set select the tab.
     */
    public static void setTab(Tab tab){
        ContentPanelTab cpt = (ContentPanelTab) tabContainer.getChildren().get(0);
        cpt.getSelectionModel().select(tab);
    }

    /**
     * Get the right panel width.
     * @return The rightPanelWidth.
     */
    public static Double getRightPanelWidth() {
        return rightPanelWidth;
    }
}
