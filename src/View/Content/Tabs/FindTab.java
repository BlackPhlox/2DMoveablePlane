package View.Content.Tabs;

import View.Content.Components.AutoComplete;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import Model.PathType;

/**
 * Set up det FindTab to find the point on the map, which is searched for.
 * Extends the Tab class.
 */
public class FindTab extends Tab {

    /**
     * Names the tab.
     * Uses auto complete and VBox.
     */
    public FindTab(){
        super("Find");
        AutoComplete autoComplete;
        autoComplete = new AutoComplete(PathType.FINDPOINT);
        VBox vb = new VBox(autoComplete);
        vb.setPadding(new Insets(5,40,5,5));
        autoComplete.prefWidthProperty().bind(vb.widthProperty());
        setContent(vb);
        setClosable(false);
    }
}
