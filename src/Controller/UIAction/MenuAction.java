package Controller.UIAction;

import Controller.UIAction.WindowAction.AlertTemplate;

/**
 * If the program is going to be closed, then the quit alert will pop up.
 */
public class MenuAction{
    /**
     * The information of the quitAlert from the AlertTemplate.
     */
    public static void quitAlert(){
        AlertTemplate.quitAlert(
                "Exiting program",
                "Are you sure you wanna close the program?",
                "Press OK to close the program");
    }

}
