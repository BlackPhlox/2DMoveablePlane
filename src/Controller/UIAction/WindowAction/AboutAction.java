package Controller.UIAction.WindowAction;

import View.WindowView;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;

/**
 * AboutAction is so the details about the program and the developers are available for the user.
 */
public class AboutAction {
    private static boolean showAboutWindowStatus;
    private static File img = new File("data/CartographerLogo01.png");

    /**
     * Show information of the creators and version.
     * Create a window for the creators and version to be in.
     */
    public static void showAbout(){
        String url;
        if(showAboutWindowStatus) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Program ");
        if(WindowView.isJAR()) {
            sb.append("Build");
            url = WindowView.class.getResource("/data/CartographerLogo01.png").toExternalForm();
        } else url = "file:///" + img.getAbsolutePath().replace("\\", "/");

        sb.append("Version: ");
        sb.append(WindowView.getVersion()).append("\n");
        sb.append("Last Update: ").append(WindowView.getProgramDate()).append("\n");
        sb.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        sb.append("OS: ").append(System.getProperty("os.name")).append("\n");
        sb.append("OS Version: ").append(System.getProperty("os.version")).append("\n");
        sb.append("Username: ").append("\n").append(System.getProperty("user.name")).append("\n");
        Label textInfo = new Label(sb.toString());
        String lsb = (" Creators:" + "\n") +
                " Lily Li" + "\n" +
                " Freja Krüger" + "\n" +
                " Frederik Martini" + "\n" +
                " Magnus Hermansen" + "\n" +
                " Mikkel Luja Rasmussen" + "\n";
        Label text = new Label(lsb);

        textInfo.setStyle("-fx-text-alignment: left");
        text.setStyle("-fx-text-alignment: right");
        text.setPadding(new Insets(0,0,0,-50));

        Image image = new Image(url);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setX(5);
        imageView.setFitHeight(55.4);
        imageView.setFitWidth(250);

        BorderPane content = new BorderPane();
        content.setLeft(textInfo);
        content.setRight(text);
        content.setTop(imageView);
        content.setPadding(new Insets(5));

        Group root = new Group();
        root.getChildren().add(content);
        Scene scene = new Scene(root, 260, 190);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("About");
        stage.setScene(scene);
        stage.show();
        showAboutWindowStatus = true;
        stage.setOnCloseRequest(e -> showAboutWindowStatus = false);
    }
}