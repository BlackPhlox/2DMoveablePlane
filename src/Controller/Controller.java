package Controller;

import Controller.InputAction.KeyboardController;
import Controller.InputAction.MouseController;
import Model.Model;
import View.Content.MapContext.SwingView;
import View.ContentView;
import View.Content.MapContext.MapView;
import View.WindowView;

import java.util.Map;

/**
 * This class handles all user input and is
 * also handling Model and View.
 */
public class Controller{
    private Model model;
    private WindowView view;
    private MapView mapView;

    /**
     * Constructor for the controller. Creates the necessary for usage.
     * @param view Takes the view to create the view.
     */
    public Controller(WindowView view){
        this.model = Model.getInstance();
        this.view = view;
        this.mapView = WindowView.getContentView().getMapView() ;
        KeyboardController keyboardController = new KeyboardController(MapView.getSwingView());
        MouseController mouseController = new MouseController(MapView.getSwingView());
    }

    /**
     * Get the model.
     * @return The model.
     */
    public Model getModel() {
        return model;
    }

    /**
     * Get the view.
     * @return The ContentView from WindowView.
     */
    public ContentView getView() {
        return WindowView.getContentView();
    }
}

