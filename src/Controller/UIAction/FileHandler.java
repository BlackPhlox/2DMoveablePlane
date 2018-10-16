package Controller.UIAction;

import Model.Graph.DirectedEdge;
import Model.Graph.EdgeWeightedDigraph;
import Model.Model;
import View.Content.MapContext.MapView;
import View.Content.MapContext.SwingView;
import View.WindowView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds the functionality needed for working with files.
 */
public class FileHandler{
    /**
     * Makes a stage where a file can be imported.
     * @param s Takes a stage to import.
     */
    public static void importFile(Stage s){
        FileChooser fc = configureFileChooser(FileAction.IMPORT);
        File file = fc.showOpenDialog(s); //stage
        if (file != null) {
            Model model = Model.getInstance();
            EdgeWeightedDigraph.setAdj(null);
            model.fileLoad(file.toString());
            MapView.resetView();
        }
    }

    /**
     * Makes a stage where a file can be saved.
     * @param s Takes a stage, which will be saved.
     */
    public static void saveFile(Stage s){
        FileChooser fc = configureFileChooser(FileAction.SAVE);
        File file = fc.showSaveDialog(s); //stage
        if (file != null) {
            Model model = Model.getInstance();
            if (file.toString().endsWith(".bin") || file.toString().endsWith(".gz")){
                model.save(file.toString());
            } else {
                model.save(file.toString()+".gz");
            }
        }
    }

    /**
     * Makes a stage where a file can be loaded.
     * @param s Takes a stage to fileLoad the file.
     */
    public static void loadFile(Stage s){
        FileChooser fc = configureFileChooser(FileAction.LOAD);
        File file = fc.showOpenDialog(s); //stage
        if (file != null) {
            Model model = Model.getInstance();
            EdgeWeightedDigraph.setAdj(null);
            model.fileLoad(file.toString());
            MapView.resetView();
        }
    }

    /**
     * Configure the file chooser.
     * @param fileAction Needs a file action for knowing what to do.
     * @return The chosen action.
     */
    private static FileChooser configureFileChooser(FileAction fileAction) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
                new File(WindowView.getUserDirectory())
        );
        switch (fileAction){
            case IMPORT:
                fileChooser.setTitle("Import OSM file");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All files", "*.*"),
                        new FileChooser.ExtensionFilter("OSM", "*.osm"),
                        new FileChooser.ExtensionFilter("Zip", "*.zip")
                );
                break;

            case LOAD:
                fileChooser.setTitle("Load GZip or Binary");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All files", "*.*"),
                        new FileChooser.ExtensionFilter("GZip ", "*.gz"),
                        new FileChooser.ExtensionFilter("Binary File", "*.bin")
                );
                break;

            case SAVE:
                fileChooser.setTitle("Save as GZip or Binary");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All files", "*.*"),
                        new FileChooser.ExtensionFilter("GZip", "*.gz"),
                        new FileChooser.ExtensionFilter("Binary File", "*.bin")
                );
                break;
        }
        return fileChooser;
    }
}
