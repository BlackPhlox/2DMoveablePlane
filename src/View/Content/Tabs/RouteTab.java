package View.Content.Tabs;

import Model.OSM.OSMAddress;
import Model.Pathfinding;
import Model.PathType;
import View.Content.Components.AutoComplete;
import View.Content.Components.PathView;
import View.Content.ContentPanel;
import View.Content.MapContext.MapView;
import View.Content.MapContext.SwingView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * RouteTab is created.
 * Extends the class Tab.
 */
public class RouteTab extends Tab {
    private static VBox searchFields;
    private static HBox searchFieldFrom, searchFieldTo, searchFieldBreakpoint;
    private ToggleGroup transportSelectionGroup;
    private ToggleGroup routeTypeSelectionGroup;

    /**
     * Construct the route tab.
     * Names the tab.
     * Adds functionality to the tab.
     */
    public RouteTab(){
        super("Route");
        Button startGuidanceBTN;
        transportSelectionGroup = new ToggleGroup();
        RadioButton walk = new RadioButton("Walk"),
                bike = new RadioButton("Bike"),
                car = new RadioButton("Car");
        walk.setUserData("Walk");
        bike.setUserData("Bike");
        car.setUserData("Car");
        walk.setToggleGroup(transportSelectionGroup);
        bike.setToggleGroup(transportSelectionGroup);
        car.setToggleGroup(transportSelectionGroup);
        car.setSelected(true);

        routeTypeSelectionGroup = new ToggleGroup();
        RadioButton fastest = new RadioButton("Fastest"), shortest = new RadioButton("Shortest");
        fastest.setUserData("Fastest");
        shortest.setUserData("Shortest");
        fastest.setToggleGroup(routeTypeSelectionGroup);
        shortest.setToggleGroup(routeTypeSelectionGroup);
        shortest.setSelected(true);

        transportSelectionGroup.selectedToggleProperty().addListener(( observable, oldValue, newValue) ->{
            if(newValue.equals(walk) || newValue.equals(bike)){
                shortest.setSelected(true);
                fastest.setDisable(true);
            } else {
                fastest.setDisable(false);
            }
        });

        searchFields = new VBox();
        searchFieldFrom = createSearchField("From", PathType.START);
        searchFieldTo = createSearchField("To", PathType.END);
        searchFields.getChildren().add(searchFieldFrom);
        searchFields.getChildren().add(searchFieldTo);

        Button flipInputBtn = new Button("Switch");
        flipInputBtn.setMinWidth(60);
        searchFieldAppend((HBox) searchFields.getChildren().get(0),flipInputBtn);
        flipInputBtn.setOnAction(e -> {
            AutoComplete from = getSearchField(searchFieldFrom);
            AutoComplete to = getSearchField(searchFieldTo);
            if (from.getSelectedAddress() != null && to.getSelectedAddress() != null) swapInput(from,to);

            MapView.getSwingView().repaint();
        });

        Button addSearchFieldBtn = new Button("Add");
        addSearchFieldBtn.setOnAction(event ->pressAddButton(searchFields));
        addSearchFieldBtn.setMinWidth(60);
        searchFieldAppend((HBox)searchFields.getChildren().get(1),addSearchFieldBtn);

        Label transportLabel = new Label("Transport: ");

        HBox transportSelection = new HBox(transportLabel, walk, bike, car);
        transportSelection.setSpacing(5);
        transportSelection.setPadding(new Insets(5));
        transportSelection.setAlignment(Pos.CENTER);

        Label typeLabel = new Label("Route: ");

        HBox routeTypeSelection = new HBox(typeLabel, shortest, fastest);
        routeTypeSelection.setSpacing(5);
        routeTypeSelection.setPadding(new Insets(5));
        routeTypeSelection.setAlignment(Pos.CENTER);

        startGuidanceBTN = new Button("GO!");
        startGuidanceBTN.setPrefWidth(Double.MAX_VALUE);
        startGuidanceBTN.setMinWidth(Double.MIN_VALUE);
        startGuidanceBTN.setOnAction(e -> {
            PathView.clear();
            String s1 = transportSelectionGroup.getSelectedToggle().getUserData().toString();
            String s2 = routeTypeSelectionGroup.getSelectedToggle().getUserData().toString();

            OSMAddress breakPointAddress = null;
            if (getSearchFieldBreakpoint() != null) breakPointAddress = getSearchFieldBreakpoint().getSelectedAddress();

            Pathfinding.routeSearch(s1, s2, getSearchField(searchFieldFrom).getSelectedAddress(), getSearchField(searchFieldTo).getSelectedAddress(), breakPointAddress);
        });

        PathView pathView = PathView.getInstance();

        VBox userInput = new VBox(
                searchFields,
                transportSelection,
                routeTypeSelection,
                startGuidanceBTN
        );

        VBox content = new VBox(userInput,pathView);
        content.setPadding(new Insets(5,40,5,5));
        setContent(content);
        setClosable(false);

        pathView.prefHeightProperty().bind(content.heightProperty().subtract(userInput.getHeight()+178));

        getSearchField(searchFieldFrom).getEditor().textProperty().addListener(e-> ContentPanel.setTab(this));

        getSearchField(searchFieldTo).getEditor().textProperty().addListener(e-> ContentPanel.setTab(this));
    }

    /**
     * Adds a new breakpoint search field.
     * @param searchFields Used to create a new search field.
     */
    public static void pressAddButton(VBox searchFields) {
        if (searchFields.getChildren().size() < 3) {
            searchFieldBreakpoint = createNewSearchField(searchFields);}
    }

    /**
     * Swaps the input.
     * @param to The to address to be swapped with the from.
     * @param from The from address to be swapped with the to.
     */
    private void swapInput(AutoComplete to, AutoComplete from) {
        if (SwingView.getMarkingMap().get(PathType.START) == from.getSelectedAddress()) {
            SwingView.getMarkingMap().put(PathType.START, to.getSelectedAddress());
            SwingView.getMarkingMap().put(PathType.END, from.getSelectedAddress());
        } else {
            SwingView.getMarkingMap().put(PathType.START, from.getSelectedAddress());
            SwingView.getMarkingMap().put(PathType.END, to.getSelectedAddress());
        }

        OSMAddress tmp = getSearchField(searchFieldFrom).getSelectedAddress();
        getSearchField(searchFieldFrom).setSelectedAddress(getSearchField(searchFieldTo).getSelectedAddress());
        getSearchField(searchFieldTo).setSelectedAddress(tmp);
    }

    /**
     * Creates a new search field.
     * @param vb Uses the VBox to add the search field over the first.
     * @return The searchField.
     */
    private static HBox createNewSearchField(VBox vb){
        HBox searchField = createSearchField("...", PathType.BREAKPOINT);
        Button re = new Button("Remove");
        re.setOnAction(e -> {
            removeSearchField(vb,searchField);
            SwingView.getMarkingMap().remove(PathType.BREAKPOINT);
            MapView.getSwingView().repaint();
        });
        re.setMinWidth(60);
        searchFieldAppend(searchField,re);
        vb.getChildren().add(1,searchField);
        return searchField;
    }

    /**
     * Remove search field.
     * @param vb Uses VBox to remove the correct searchField.
     * @param searchField Is a HBox which is used as identification for deletion.
     */
    private static void removeSearchField(VBox vb,HBox searchField){
        for(int i = 0; i < vb.getChildren().size(); i++){
            if(vb.getChildren().get(i).hashCode() == searchField.hashCode()){
                vb.getChildren().remove(i);
            }
        }
    }

    /**
     * Creates a customized search field.
     * Functionality is not fully accomplished, but it is a step towards gaining the ability to create breakpoints.
     * @param name The name of the label.
     * @param pathType Used as the type for the point.
     * @return HBox.
     */
    private static HBox createSearchField(String name, PathType pathType){
        Label nameText = new Label(name);
        nameText.setMinWidth(50);
        nameText.setAlignment(Pos.CENTER_RIGHT);
        AutoComplete tf = new AutoComplete(pathType);
        tf.setPrefWidth(1000);
        HBox hb = new HBox(nameText,tf);
        hb.setAlignment(Pos.CENTER_LEFT);
        return hb;
    }

    /**
     * Append the search field with the nodes.
     * @param hb Uses the HBox as input field for the nodes.
     * @param n Places between the first and the last node.
     */
    private static void searchFieldAppend(HBox hb, Node n){
        hb.getChildren().add(2,n);
    }

    /**
     * @param hb Get search field.
     * @return The node.
     */
    private static AutoComplete getSearchField(HBox hb){
        if (hb == null) return null;
        Node node = hb.getChildren().get(1);
        if(node instanceof AutoComplete){
            return (AutoComplete) node;
        }
        return null;
    }

    /**
     * Get search field to.
     * @return The searchFieldTo.
     */
    public static AutoComplete getSearchFieldTo() {
        return getSearchField(searchFieldTo);
    }

    /**
     * Get search field from.
     * @return The searchFieldFrom.
     */
    public static AutoComplete getSearchFieldFrom() {
        return getSearchField(searchFieldFrom);
    }

    /**
     * Get search field breakpoint.
     * @return The searchFieldBreakpoint.
     */
    public static AutoComplete getSearchFieldBreakpoint() {
        return getSearchField(searchFieldBreakpoint);
    }

    /**
     * Get search fields.
     * @return The searchFields.
     */
    public static VBox getSearchFields() {
        return searchFields;
    }
}
