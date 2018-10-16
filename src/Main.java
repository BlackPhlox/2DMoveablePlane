import View.WindowView;
import javafx.application.Application;


public class Main {
    /**
     * Use JavaFX.
     * Launch WindowView.
     *
     * @param args Arguments for the program when opened.
     */
    public static void main(String[] args){
        if(WindowView.isJAR()) System.out.println("Launching " + WindowView.getStageTitle() + " - Jar Edition");
        else System.out.println("Launching " + WindowView.getStageTitle());
        if(args.length > 0) {
            WindowView.setDebugging(true);
            System.out.println("Debugging Mode Enabled");
        } else {
            WindowView.setDebugging(false);
        }
        System.out.println("---------------------");
        System.setProperty("javafx.embed.singleThread", "true");
        Application.launch(WindowView.class, args);
    }
}
