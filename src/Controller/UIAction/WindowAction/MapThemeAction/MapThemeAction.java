package Controller.UIAction.WindowAction.MapThemeAction;

import Model.OSM.OSMWayType;
import View.Content.MapContext.MapView;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.Map;

/**
 * Makes the map UI possible to change and personalize.
 * Uses OSMColor and OSMTheme, to personalize or to have different themes to choose between.
 */
public class MapThemeAction {
    private static boolean showMapThemeSelectStatus;

    /**
     * Creates a window with the default theme, can switch between pre-defined themes located in the comboBox
     * or create your own using the designated themes called "Custom [ID]"
     */
    public static void showMapThemeSelect(){
        if(showMapThemeSelectStatus) return;
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Choose a map theme");

        Button saveTheme = new Button("Save");
        saveTheme.setOnAction(e3 -> {
            showMapThemeSelectStatus = false;
            stage.close();
        });

        ListView<OSMColor> colorMap = new ListView<>();

        VBox root = new VBox();
        ComboBox<OSMTheme> mapTheme = new ComboBox<>();

        //Themes
        OSMTheme defaultTheme = new OSMTheme("Default");
        OSMTheme nightTheme = new OSMTheme("Night Theme");
        nightTheme.setOSMColor(new OSMColor(OSMWayType.BACKGROUND, new java.awt.Color(100,100,100),false));
        nightTheme.setOSMColor(new OSMColor(OSMWayType.BUILDING, new java.awt.Color(0, 0, 0),false));
        nightTheme.setOSMColor(new OSMColor(OSMWayType.WATER, new java.awt.Color(100,100,100),false));
        nightTheme.setOSMColor(new OSMColor(OSMWayType.COASTLINE, new java.awt.Color(60,60,60),false));
        nightTheme.setOSMColor(new OSMColor(OSMWayType.ROAD, new java.awt.Color(200,200,200),false));
        nightTheme.setOSMColor(new OSMColor(OSMWayType.LANDUSE, new java.awt.Color(0, 75, 0),false));
        nightTheme.setOSMColor(new OSMColor(OSMWayType.PARK, new java.awt.Color(36, 94, 0),false));
        OSMTheme aidTheme = new OSMTheme("Visual Aid");
        for (int i = 0; i < OSMWayType.values().length; i++) {
            if(OSMWayType.values()[i].getColor() != null){
                aidTheme.setOSMColor(new OSMColor(OSMWayType.values()[i], OSMWayType.values()[i].getColor(),true));
            }
        }

        OSMTheme martiniTheme = new OSMTheme("Martini Theme");
        martiniTheme.setOSMColor(new OSMColor(OSMWayType.BACKGROUND, new java.awt.Color(74, 137, 243),false));
        martiniTheme.setOSMColor(new OSMColor(OSMWayType.FOOTWAY, new java.awt.Color(247, 130, 0),false));
        martiniTheme.setOSMColor(new OSMColor(OSMWayType.BUILDING, new java.awt.Color(172, 169, 151),false));
        martiniTheme.setOSMColor(new OSMColor(OSMWayType.RAILWAY, new java.awt.Color(255, 255, 164),false));
        martiniTheme.setOSMColor(new OSMColor(OSMWayType.PARK, new java.awt.Color(193, 237, 173),false));
        martiniTheme.setOSMColor(new OSMColor(OSMWayType.FERRY, new java.awt.Color(0, 14, 255),false));

        OSMTheme customTheme01 = new OSMTheme("Custom 01");
        for (int i = 0; i < OSMWayType.values().length; i++) {
            if(OSMWayType.values()[i].getColor() != null){
                customTheme01.setOSMColor(new OSMColor(OSMWayType.values()[i], OSMWayType.values()[i].getColor(),true));
            }
        }

        mapTheme.getSelectionModel().selectedItemProperty().addListener(( observable, oldValue, newValue) -> {
            colorMap.getItems().clear();
            for (Object o : newValue.getTheme().entrySet()) {
                Map.Entry element = (Map.Entry) o;
                OSMColor c = (OSMColor) element.getValue();
                colorMap.getItems().add(c);
                c.update();
            }
            MapView.getSwingView().repaint();
        });

        mapTheme.getItems().addAll(
                defaultTheme,nightTheme,martiniTheme,aidTheme,customTheme01
        );

        mapTheme.getSelectionModel().select(defaultTheme);

        mapTheme.prefWidthProperty().bind(root.widthProperty());
        Label presetLabel = new Label("Presets");
        presetLabel.setMinWidth(50);
        presetLabel.setPadding(new Insets(4,0,5,0));
        HBox themePreset = new HBox(presetLabel, mapTheme);
        themePreset.setPadding(new Insets(5));
        root.getChildren().addAll(themePreset,colorMap, saveTheme);
        Scene scene = new Scene(root,250, 300);

        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        showMapThemeSelectStatus = true;
        stage.setOnCloseRequest(e -> showMapThemeSelectStatus = false);
    }
}
