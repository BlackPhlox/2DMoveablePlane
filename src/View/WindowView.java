package View;

import Controller.Controller;
import Model.Model;
import Model.PointOfInterest;
import View.Content.Tabs.PoiTab;
import View.Content.MapContext.MapView;
import View.Content.Components.AutoComplete;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.*;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * This class creates the window of the program and sets up the MVC when the
 * selected map have been selected and loaded.
 */
public class WindowView extends Application {
    private final static double VERSION = 1.1;
    private final static String PROGRAM_DATE = "17/05/2018";
    private final static String STAGE_TITLE = "Cartographer V" + VERSION;
    private final static String DATA_FOLDER_NAME = "CartographerData";
    private final static String USER_DIRECTORY = System.getProperty("user.dir");
    private final static boolean JAR = true;
    private final static File img = new File("data/CartographerLogo01.png");
    private final static File WINDOW_LOGO = new File("data/CartographerLogoIcon01.png");

    private static DecimalFormatSymbols localDecimalFormat = new DecimalFormatSymbols(Locale.getDefault());
    private static boolean debugging;
    private static int width, height;
    private static Scene scene;
    private static Stage stage;
    private static WindowView instance;
    private static ContentView contentView;

    /**
     * Gets the istance of WindowView.
     * @return The instance of the WindowView.
     */
    public static WindowView getInstance(){
        if(instance  == null) instance = new WindowView();
        return instance;
    }

    public static int getHeight() {
        return height;
    }

    public static int getWidth() {
        return width;
    }

    /**
     * Starts the primary stage with everything necessary
     * @param primaryStage Uses a Stage for startup of the window.
     */
    @Override
    public void start(Stage primaryStage) {
        width = 800;
        height = 600;

        Model model = Model.getInstance();

        stage = primaryStage;
        primaryStage.setOnShowing(e -> {
            new MapView(width,height);
            contentView = new ContentView(primaryStage, width, height);
            Controller controller = new Controller(this);
            scene = new Scene(controller.getView(), width, height);
            ThemeManager.setScene(scene);
            ThemeManager.loadTheme(ThemeManager.getBlackTheme(),true);

            scene.getStylesheets().add(getEmbeddedFileString(ThemeManager.programStyle));

            primaryStage.setTitle(STAGE_TITLE);
            primaryStage.setScene(scene);

            if(!stage.isIconified()){
                stage.getIcons().add(new Image(getEmbeddedFileString(WINDOW_LOGO)));
            }

            stage.setScene(scene);
            stage.setAlwaysOnTop(false);

            ThemeManager.loadDefault();

            if(isJAR()) {
                if(ThemeManager.fileExist(getDataFolderName()+"/"+"PointsOfInterest.gz")){
                    Model.setMapPois(model.loadObject(model.getMapPois().getClass(),"PointsOfInterest"));
                }
            } else {
                if(ThemeManager.fileExist(getDataFolderName()+"\\"+"PointsOfInterest.gz")){
                    Model.setMapPois(model.loadObject(model.getMapPois().getClass(),"PointsOfInterest"));
                }
            }

            PoiTab.loadPois();
        });


        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            if (isDebugging())
                System.out.println("Height: " + primaryStage.getHeight() + " Width: " + primaryStage.getWidth());
            width = (int) primaryStage.getWidth();
            height = (int) primaryStage.getHeight();
            MapView.resizeView(width, height);
        };

        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);

        primaryStage.setMinHeight(280);
        primaryStage.setMinWidth(355);

        primaryStage.setOnCloseRequest(e -> {

            ThemeManager.deleteFile("temp.css");

            model.getMapPois().clear();
            for(PointOfInterest poi : PoiTab.getUiPois().getItems()){
                model.getMapPois().add(poi);
            }
            if(model.getMapPois().size() > 0){
                model.saveObject(model.getMapPois(),"PointsOfInterest");
            } else {
                ThemeManager.deleteFile("PointsOfInterest.gz");
            }
            Platform.exit();
            System.exit(0);
        });

        Image image = new Image(getEmbeddedFileString(img));
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setX(5);
        imageView.setFitHeight(55.4);
        imageView.setFitWidth(250);

        Label title = new Label();
        title.setTextAlignment(TextAlignment.CENTER);
        Button loadDk = new Button("Load Denmark");
        Button loadBh = new Button("Load Bornholm");
        HBox buttonBox = new HBox(loadDk,loadBh);
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);
        VBox root = new VBox(imageView, title, buttonBox);
        root.setSpacing(2);
        root.setAlignment(Pos.CENTER);
        Scene preScene = new Scene(root, 300, 150);
        Stage preStage = new Stage();
        preStage.setScene(preScene);
        preStage.initStyle(StageStyle.UTILITY);
        preStage.setTitle("Select a map to start the program");
        preStage.setAlwaysOnTop(true);
        preStage.show();
        preStage.centerOnScreen();
        preStage.setY(preStage.getY()+150);

        loadBh.setOnAction(e->{
            preStage.close();
            Model.getInstance().load("bh.gz");
            openMainWindowView();
        });

        loadDk.setOnAction(e->{
            preStage.close();
            Model.getInstance().load("dk.gz");
            openMainWindowView();
        });

    }

    /**
     * Open main window view.
     */
    private void openMainWindowView(){
        stage.show();
    }

    /**
     * Get the scenes.
     * @return The scene.
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * Get the stage.
     * @return The stage.
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Get the version.
     * @return The version.
     */
    public static double getVersion() {
        return VERSION;
    }

    /**
     * Get the program date.
     * @return The program date.
     */
    public static String getProgramDate() {
        return PROGRAM_DATE;
    }

    /**
     * Sets the debugging.
     * @param b Uses a boolean to set the debugging.
     */
    public static void setDebugging(boolean b){
        debugging = b;
    }

    /**
     * Get the boolean for the debugging.
     * @return The debugging boolean.
     */
    public static boolean isDebugging(){
        return debugging;
    }

    /**
     * Get the boolean for the program JAR.
     * @return The JAR boolean.
     */
    public static boolean isJAR() {
        return JAR;
    }

    /**
     * Get the contentView.
     * @return The contentView.
     */
    public static ContentView getContentView() {
        return contentView;
    }

    /**
     * Get the data folder name.
     * @return The data folder name.
     */
    public static String getDataFolderName() {
        return DATA_FOLDER_NAME;
    }

    /**
     * Get the user directory.
     * @return The user directory.
     */
    public static String getUserDirectory() {
        return USER_DIRECTORY;
    }

    /**
     * Get the local decimal format.
     * @return The local decimal format.
     */
    public static DecimalFormatSymbols getLocalDecimalFormat() {
        return localDecimalFormat;
    }

    /**
     * Get the stage title.
     * @return The stage title.
     */
    public static String getStageTitle() {
        return STAGE_TITLE;
    }

    public static void debugPrintln(String string){
        System.out.println(getCurrentTimeStamp()+ " : " + string);
    }

    private static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static String getFileString(File file){
        if(WindowView.isJAR()){
            return "file:///" + file.getAbsolutePath().replace("\\","/");
        } else {
            return "file:///" + file.getAbsolutePath().replace("/", "\\");
        }
    }

    public static String getEmbeddedFileString(File file){
        if(WindowView.isJAR()){
            String dir = ("/"+file.toString()).replace("\\", "/");
            return WindowView.class.getResource(dir).toExternalForm();
        } else {
            return "file:///" + file.getAbsolutePath().replace("/", "\\");
        }
    }
}
