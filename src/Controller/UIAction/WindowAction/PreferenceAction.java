package Controller.UIAction.WindowAction;

import Controller.InputAction.KeyboardController;
import Model.Model;
import Model.OSM.OSMWayType;
import View.Content.Components.ZoomSlider;
import View.Content.MapContext.MapView;
import View.Content.MapContext.SwingView;
import View.WindowView;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static Model.Model.getOptions;

/**
 * Enables the user to choose which options should be available.
 * So that the program will fit the user.
 */
public class PreferenceAction {
    private static boolean showPrefStatus;
    private Option aaOption = null;
    private int nonDebugOptions;
    private static boolean aa = true, aaWhenMoving = true, smoothZoom = true;

    /**
     * Gives the boolean of smoothZoom.
     * @return Boolean of smoothZoom.
     */
    public static boolean isSmoothZoom() {
        return smoothZoom;
    }

    /**
     * Gives the boolean of antialiasing.
     * @return Boolean of antialiasing.
     */
    public static boolean isAa() {
        return aa;
    }

    /**
     * Gives the boolean of antialiasing when moving.
     * @return Boolean of antialiasing when moving.
     */
    public static boolean isAaWhenMoving() {
        return aaWhenMoving;
    }

    /**
     * Shows the preferences.
     */
    public void showPref(){
        ListView<Option> options;
        if(showPrefStatus) return;
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Preferences");

        options = getOptions();
        nonDebugOptions = Model.getNonDebugOptions();

        if(options.getItems().isEmpty()){
            addDefaultOptions(options);
            Model.setNonDebugOptions(options.getItems().size());
            if(WindowView.isDebugging()){
                addDebugOptions(options);
            }
        } else {
            if(WindowView.isDebugging()){
                if (nonDebugOptions >= options.getItems().size()) {
                    addDebugOptions(options);
                }
                for (int i = nonDebugOptions; i < options.getItems().size(); i++) {
                    Option option = options.getItems().get(i);
                    option.setVisible(true);
                }
            } else {
                for (int i = nonDebugOptions; i < options.getItems().size(); i++) {
                    Option option = options.getItems().get(i);
                    option.setVisible(false);
                }
            }
        }

        VBox root = new VBox(options);
        Scene scene = new Scene(root,300, 300);
        options.prefHeightProperty().bind(root.heightProperty());
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.show();
        showPrefStatus = true;
        stage.setOnCloseRequest(e -> showPrefStatus = false);
    }

    /**
     * Adds to the default options.
     * @param options Uses options to add to the ListView<Option>, so that all options necessary are there.
     */
    private void addDefaultOptions(javafx.scene.control.ListView options) {
        //Add additional options here
        options.getItems().add(new Option(
                "Use Smooth Zoom",
                new String[]{"Yes","No"},
                (observable, oldValue, newValue) -> smoothZoom = newValue.equals("Yes")
        ));

        options.getItems().add(new Option(
                "Zoom Slider",
                new String[]{"Show","Hide"},
                (observable, oldValue, newValue) -> {
                    if(newValue.equals("Show")){
                        ZoomSlider.getInstance().setVisible(true);
                    } else {
                        ZoomSlider.getInstance().setVisible(false);
                    }
                }
        ));

        options.getItems().add(new Option(
                "Scale Bar",
                new String[]{"Show","Hide"},
                (observable, oldValue, newValue) -> {
                    if(newValue.equals("Show")){
                        SwingView.setShowScalebar(true);
                    } else {
                        SwingView.setShowScalebar(false);
                    }
                    MapView.getSwingView().repaint();
                }
        ));

        options.getItems().add(new Option(
                "Tooltip",
                new String[]{"Show","Hide"},
                (observable, oldValue, newValue) -> {
                    if(newValue.equals("Show")){
                        SwingView.setShowToolTip(true);
                    } else {
                        SwingView.setShowToolTip(false);
                    }
                    MapView.getSwingView().repaint();
                }
        ));

        options.getItems().add(new Option(
                "Movement Speed",
                new String[]{"Slow","Medium","Fast"},
                (observable, oldValue, newValue) -> {
                    switch (newValue) {
                        case "Slow":
                            KeyboardController.setRegSpeed(2);
                            break;
                        case "Medium":
                            KeyboardController.setRegSpeed(3);
                            break;
                        case "Fast":
                            KeyboardController.setRegSpeed(5);
                            break;
                    }
                }
        ));

        options.getItems().add(new Option(
                "Shift Speed Multiplier",
                new String[]{"Slow (200%)","Medium (300%)","Fast(400%)"},
                (observable, oldValue, newValue) -> {
                    switch (newValue) {
                        case "Slow (200%)":
                            KeyboardController.setShiftSpeed(2);
                            break;
                        case "Medium (300%)":
                            KeyboardController.setShiftSpeed(3);
                            break;
                        case "Fast (400%)":
                            KeyboardController.setShiftSpeed(4);
                            break;
                    }
                }
        ));

        options.getItems().add(new Option(
                "Coordinates",
                new String[]{"None","Real","Screen","Model","All"},
                ((observable, oldValue, newValue) -> {
                    switch (newValue){
                        case "None": SwingView.setShowCoordinates(false);SwingView.setCoordinateType(0);break;
                        case "Real": SwingView.setShowCoordinates(true);SwingView.setCoordinateType(1);break;
                        case "Screen" : SwingView.setShowCoordinates(true);SwingView.setCoordinateType(2);break;
                        case "Model": SwingView.setShowCoordinates(true);SwingView.setCoordinateType(3);break;
                        case "All" : SwingView.setShowCoordinates(true);SwingView.setCoordinateType(4);break;
                    }
                    MapView.getSwingView().repaint();
                })
        ));

        options.getItems().add(new Option(
                "Use Antialiasing",
                new String[]{"No","Yes"},
                (observable, oldValue, newValue) ->{
                    if (newValue.equals("Yes")){
                        aa = true;
                        aaOption = new Option(
                                "Use AA when moving",
                                new String[]{"No","Yes"},
                                (observable1, oldValue1, newValue1) ->{
                                    aaWhenMoving = newValue1.equals("No");
                                    MapView.getSwingView().repaint();
                                }
                        );
                        options.getItems().add(nonDebugOptions,aaOption);
                    } else {
                        aa = false;
                        if(aaOption != null) options.getItems().remove(aaOption);
                    }
                    MapView.getSwingView().repaint();
                }
        ));

        nonDebugOptions =  options.getItems().size();
    }

    /**
     * Add to the debug options.
     * @param options Uses options to add debug options to the ListView<Option>
     */
    private void addDebugOptions(javafx.scene.control.ListView options){
        options.getItems().add(new Option(
                "Debug Viewport Size",
                new String[]{"Big","Medium","Small","Off"},
                (observable, oldValue, newValue) ->{
                    switch (newValue){
                        case "Small": SwingView.setDebugViewportSizePct(0.10); break;
                        case "Medium": SwingView.setDebugViewportSizePct(0.20); break;
                        case "Big": SwingView.setDebugViewportSizePct(0.25); break;
                        default: SwingView.setDebugViewportSizePct(0); break;
                    }
                    MapView.getSwingView().repaint();
                }
        ));

        options.getItems().add(new Option(
                "Level Of Detail",
                new String[]{"Yes","No"},
                ((observable, oldValue, newValue) -> {
                    switch (newValue){
                        case "Show": SwingView.setShowLOD(true);break;
                        case "Hide" : SwingView.setShowLOD(false);break;
                    }
                    MapView.getSwingView().repaint();
                })
        ));

        options.getItems().add(new Option(
                "KD-Tree",
                new String[]{"Show", "Hide"},
                ((observable, oldValue, newValue) -> {
                    if(newValue.matches("Show")){
                        SwingView.setShowKD(true);
                    } else {
                        SwingView.setShowKD(false);
                    }
                    MapView.getSwingView().repaint();
                })
        ));

        options.getItems().add(new Option(
                "KD-Tree of",
                enumToStringArr(OSMWayType.values()),
                ((observable, oldValue, newValue) -> {
                    SwingView.setKdType(OSMWayType.valueOf(newValue));
                    MapView.getSwingView().repaint();
                })
        ));
    }

    /**
     * Makes enum array to String array.
     * @param enums Takes an enum array.
     * @return A String array.
     */
    private static String[] enumToStringArr(Enum[] enums){
        String[] strings = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            strings[i] = enums[i].name();
        }
        return strings;
    }
}
