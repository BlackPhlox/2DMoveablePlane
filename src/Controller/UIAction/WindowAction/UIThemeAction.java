package Controller.UIAction.WindowAction;

import Model.CSSFile;
import View.ThemeManager;
import View.WindowView;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Sets the UI theme.
 */
public class UIThemeAction {
    //Keeps track of which windows is open which prevent multiple instances of the same window
    private static boolean
            showInterfaceThemeSelectStatus;

    /**
     * Creates the theme selection interface.
     */
    public static void showInterfaceThemeSelect(){
        if(showInterfaceThemeSelectStatus) return;
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("Select a Color Theme");

        ComboBox<CSSFile> uiThemes = new ComboBox<>();
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(165);
        colorPicker.setOnAction(e2 -> {
            uiThemes.getSelectionModel().clearSelection();
            ThemeManager.createTempTheme(colorPicker.getValue());
        });

        TextField uiThemeName = new TextField();
        Button saveTheme = new Button("Save");
        Button setDefault = new Button("Set Default");
        uiThemes.setPrefWidth(165);
        setDefault.setMinWidth(80);
        saveTheme.setMinWidth(85);


        uiThemes.getItems().add(CSSFile.valueOf(ThemeManager.getBlueTheme()));
        uiThemes.getItems().add(CSSFile.valueOf(ThemeManager.getBlackTheme()));
        uiThemes.getItems().add(CSSFile.valueOf(ThemeManager.getWhiteTheme()));

        String dirSymbol;
        if(WindowView.isJAR()){
            dirSymbol = "/";
        } else {
            dirSymbol = "\\";
        }

        if(new File(WindowView.getUserDirectory()+dirSymbol+WindowView.getDataFolderName()).exists()){
            CSSFile dir = new CSSFile(WindowView.getUserDirectory()+dirSymbol+WindowView.getDataFolderName());
            for(int i = 0; i < dir.listFiles().length; i++){
                CSSFile css = CSSFile.valueOf(dir.listFiles()[i]);
                if(css != null){
                    uiThemes.getItems().add(css);
                }
            }
        }

        uiThemes.getSelectionModel().selectedItemProperty().addListener(( observable, oldValue, newValue) -> {
            if (newValue != null){
                boolean isEmbedded = false;
                String name = newValue.getName().replace("/","");
                if (
                    ThemeManager.getBlackTheme().getName().equals(name)||
                    ThemeManager.getWhiteTheme().getName().equals(name)||
                    ThemeManager.getBlueTheme().getName().equals(name)) isEmbedded = true;
                if(isEmbedded){
                    ThemeManager.loadTheme(new File(newValue.getParent()+"/"+newValue+".css"), true);
                }else {
                    ThemeManager.loadTheme(newValue,false);
                }

            }
        });

        setDefault.setOnAction(e3 -> {
            ThemeManager.createDefaultTheme(colorPicker.getValue());
            uiThemeName.setPromptText("Default has been set");
        });

        saveTheme.setOnAction(e4 -> {
            if(!uiThemeName.getText().isEmpty()){
                if(uiThemeName.getText().matches("^\\s*\\S+\\s*$")){
                    showInterfaceThemeSelectStatus = false;
                    IOException error = null;
                    try{
                        ThemeManager.createCustomNamedTheme(uiThemeName.getText(),colorPicker.getValue());
                    } catch (FileNotFoundException e) {
                        error = e;
                        AlertTemplate.infoAlert("Input Error", "Invalid name of a theme", "Sadly \"" + uiThemeName.getText() + "\" is not a valid name");
                    } catch (IOException e){
                        e.printStackTrace();
                    } finally {
                        if(error == null){
                            stage.close();
                        }
                    }
                } else {
                    AlertTemplate.infoAlert(
                            "Input Error",
                            "Invalid name of a theme",
                            "Sadly \"" + uiThemeName.getText() + "\" is not a valid name " +
                                    "due to the fact that whitespaces is not allowed"
                    );
                }
            } else {
                uiThemeName.setPromptText("Name needed");
            }
        });

        VBox root = new VBox(uiThemes,colorPicker,uiThemeName,new HBox(setDefault,saveTheme));
        Scene scene = new Scene(root,165, 100);

        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        showInterfaceThemeSelectStatus = true;
        stage.setOnCloseRequest(e -> showInterfaceThemeSelectStatus = false);
    }
}
