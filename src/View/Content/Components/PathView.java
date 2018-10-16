package View.Content.Components;

import Model.DirectionType;
import Model.OSM.OSMNode;
import Model.PathType;
import View.Content.MapContext.MapView;
import View.Content.MapContext.SwingView;
import View.WindowView;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import java.io.File;

/**
 * This class is responsible for showing the directions for a destination.
 */
public class PathView extends ListView<Node>{
    private static File ahead = new File("roadsigns/ArrowUp.png");
    private static File sll = new File("roadsigns/ShiftLaneLeft.png");
    private static File slr = new File("roadsigns/ShiftLaneRight.png");
    private static File tr = new File("roadsigns/TurnRight.png");
    private static File tl = new File("roadsigns/TurnLeft.png");
    private static File end = new File("roadsigns/End.png");
    private static File start = new File("roadsigns/Start.png");
    private static PathView pathView;

    private PathView() {}

    /**
     * Get the instance of PathView.
     * @return Gives pathView.
     */
    public static PathView getInstance(){
        if(pathView == null) {
            pathView = new PathView();
        }
        return pathView;
    }

    /**
     * fileLoad image which is used to give the image view with given image.
     * @param file Needs a file to create the image.
     * @return Gives the image view with the image.
     */
    private static ImageView loadImage(File file){
        String url;
        if(WindowView.isJAR()) {
            url = WindowView.class.getResource(file.getName()).toExternalForm();
        } else url = "file:///" + file.getAbsolutePath().replace("\\", "/");
        return new ImageView(new Image(url));
    }

    /**
     * Get the path status, in hour, minutes and distance.
     * @param hour Uses hour to say how long it takes in hours.
     * @param minutes Uses the minutes to say how long it takes in minutes.
     * @param dist Uses the distances to say how long there is.
     */
    public static void pathStatus(int hour, int minutes , double dist){
        Button btn;
        String newDist = "" + ((int)dist) + " m";
        if(dist > 1000) newDist = "" + dist / 1000 + " km";
        if(hour == 0){
            btn = new Button("Minimum Time: " + minutes + " minutes\nTotal Distance: " + newDist);
        } else if (minutes == 0){
            btn = new Button("Minimum Time: " + hour + " hours\nTotal Distance: " + newDist);
        } else {

            btn = new Button("Minimum Time: " + hour + " h and "+ minutes + " min\nTotal Distance: " + newDist);
        }

        btn.setGraphic(new ImageView(new Image(WindowView.class.getResource("/roadsigns/Start.png").toExternalForm())));
        btn.setWrapText(true);
        btn.setContentDisplay(ContentDisplay.RIGHT);
        btn.setGraphicTextGap(30);
        btn.setAlignment(Pos.BASELINE_RIGHT);
        btn.setTextAlignment(TextAlignment.LEFT);

        getInstance().getItems().add(0,btn);
    }

    /**
     * Add a direction to the description.
     * @param coords Uses coords to get longitude and latitude, and make breakpoints.
     * @param dir Describes which way the user should go.
     * @param direction Used as a switch type in terms of the direction.
     */
    public static void addDirection(float[] coords, String dir, DirectionType direction){
        String img;
        switch (direction){
            case AHEAD:            img = WindowView.class.getResource("/roadsigns/ArrowUp.png").toExternalForm();break;
            case TURN_LEFT:        img = WindowView.class.getResource("/roadsigns/TurnLeft.png").toExternalForm();break;
            case TURN_RIGHT:       img = WindowView.class.getResource("/roadsigns/TurnRight.png").toExternalForm();break;
            case SWITCHLANE_LEFT:  img = WindowView.class.getResource("/roadsigns/ShiftLaneLeft.png").toExternalForm();break;
            case SWITCHLANE_RIGHT: img = WindowView.class.getResource("/roadsigns/ShiftLaneRight.png").toExternalForm();break;
            case END:              img = WindowView.class.getResource("/roadsigns/End.png").toExternalForm();break;
            default: img = null; break;
        }

        ImageView imgView = new ImageView(new Image(img));

        Button btn = new Button(dir);
        btn.setGraphic(imgView);
        btn.setWrapText(true);
        btn.setContentDisplay(ContentDisplay.RIGHT);
        btn.setGraphicTextGap(5);
        btn.setAlignment(Pos.BASELINE_RIGHT);
        btn.setTextAlignment(TextAlignment.LEFT);

        btn.setOnMousePressed(e -> {
            if(coords != null){
                switch (e.getButton()){
                    case PRIMARY: MapView.getSwingView().setAbsPosition(coords[0],coords[1]); break;
                    case SECONDARY: SwingView.getMarkingMap().put(PathType.BREAKPOINT,new OSMNode(coords[0],coords[1]));
                    MapView.getSwingView().repaint();
                    break;
                }
            }
        });
        getInstance().getItems().add(btn);
    }

    /**
     * Clears the instance.
     */
    public static void clear(){
        getInstance().getItems().clear();
    }

    /**
     * Set the button width subtraction value.
     * @param deltaWidth Uses delta as to know what to subtract with.
     */
    public static void setBtnWidthSubtractionValue(double deltaWidth){
        for(Node node : getInstance().getItems()){
            if(node instanceof Button){
                Button btn = (Button) node;
                btn.prefWidthProperty().bind(getInstance().widthProperty().subtract(deltaWidth));
            }
        }
    }
}
