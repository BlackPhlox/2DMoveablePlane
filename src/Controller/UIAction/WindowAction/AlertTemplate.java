package Controller.UIAction.WindowAction;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * AlertTemplates are used so the UI becomes more user friendly.
 */
public class AlertTemplate{
    /**
     * Create a infoAlert, which uses the ENUM: INFORMATION from Alert.java.
     * @param title Needs a title on the top of the window.
     * @param header Needs a header to let the user know what the problem is.
     * @param content Needs content to get a more described version of what is wrong.
     */
    public static void infoAlert(String title,String header,String content){
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle(title);
        infoAlert.setHeaderText(header);
        infoAlert.setContentText(content);

        Stage stage = (Stage) infoAlert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        infoAlert.showAndWait();
    }

    /**
     * Create a quitAlert, which uses the ENUM: CONFIRMATION from Alert.java.
     * @param title Needs a title on the top of the popup window.
     * @param header Needs a header to let the user know what the problem is.
     * @param content Needs content to get a more described version of what is wrong.
     */
    public static void quitAlert(String title, String header, String content){
        Alert close_popup = new Alert(Alert.AlertType.CONFIRMATION);
        close_popup.setTitle(title);
        close_popup.setHeaderText(header);
        close_popup.setContentText(content);

        Stage stage = (Stage) close_popup.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        Optional<ButtonType> result = close_popup.showAndWait();
        if(result.isPresent()){
            if (result.get() == ButtonType.OK){
                System.exit(1);
            } else {
                close_popup.close();
            }
        } else {
            close_popup.close();
        }
    }
}
