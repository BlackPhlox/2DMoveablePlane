package View.Content;

import Controller.UIAction.FileHandler;
import Controller.UIAction.MenuAction;
import Controller.UIAction.WindowAction.AboutAction;
import Controller.UIAction.WindowAction.MapThemeAction.MapThemeAction;
import Controller.UIAction.WindowAction.PreferenceAction;
import Controller.UIAction.WindowAction.UIThemeAction;
import View.Content.MapContext.MapView;
import View.Content.MapContext.SwingView;
import View.WindowView;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

/**
 * This class is the representation of the programs
 * menuBar can contains all the additional functionality
 * besides the navigation and the direct use of the map.
 */
public class WindowBar extends MenuBar {

    /**
     * Construct the WindowBar.
     * @param stage Uses the stage to make the WindowBar scalable.
     * @param swingView Uses swingView to repaint and get functionality from swingView.
     */
    public WindowBar(Stage stage, SwingView swingView) {
        final CheckMenuItem showFpsBtn, showPoisBtn;
        final MenuItem importItem, loadItem, saveItem, quitItem, aboutItem, docItem,wikiItem, mapTheme, prefItem;
        final CheckMenuItem debugItem;

        final Menu fileMenu, showMenu, optMenu, helpMenu;
        prefWidthProperty().bind(stage.widthProperty());

        fileMenu = new Menu("File");

        importItem = new MenuItem("Import");
        loadItem = new MenuItem("Load");
        saveItem = new MenuItem("Save");
        quitItem = new MenuItem("Quit");

        showMenu = new Menu("Show");
        showFpsBtn = new CheckMenuItem("Show FPS");
        showPoisBtn = new CheckMenuItem("Show Points of Interest");
        showPoisBtn.setSelected(true);
        showFpsBtn.setSelected(WindowView.isDebugging());

        optMenu = new Menu("Option");
        debugItem = new CheckMenuItem("Debug");
        debugItem.setSelected(WindowView.isDebugging());

        prefItem = new MenuItem("Preferences");

        Menu changeItem = new Menu("Change");
        mapTheme = new MenuItem("Map Theme");
        MenuItem customThemeBtn = new MenuItem("UI Theme");

        helpMenu = new Menu("Help");
        wikiItem = new MenuItem("Wiki");
        docItem = new MenuItem("Java Doc");
        aboutItem = new MenuItem("About...");

        showFpsBtn.setOnAction(e -> {
            swingView.setShowFPS(!swingView.isShowFPS());
            swingView.repaint();
        });

        showPoisBtn.setOnAction(event -> {
            SwingView.setShowPois(!SwingView.isShowPois());
            swingView.repaint();
        });

        wikiItem.setOnAction(e ->{
            if (Desktop.isDesktopSupported()) {
                openUrl("https://blackphlox.github.io/Java/Projects/BFST18_Group3/wiki/");
            }
        });

        docItem.setOnAction(e ->{
            if (Desktop.isDesktopSupported()) {
                openUrl("https://blackphlox.github.io/Java/Projects/BFST18_Group3/doc/");
            }
        });

        mapTheme.setOnAction(e -> MapThemeAction.showMapThemeSelect());

        prefItem.setOnAction(e -> new PreferenceAction().showPref());

        customThemeBtn.setOnAction(e -> UIThemeAction.showInterfaceThemeSelect());

        Stage s = WindowView.getStage();
        importItem.setOnAction(e -> FileHandler.importFile(s));
        saveItem.setOnAction(e -> FileHandler.saveFile(s));
        loadItem.setOnAction(e -> FileHandler.loadFile(s));
        quitItem.setOnAction(e -> MenuAction.quitAlert());
        aboutItem.setOnAction(e -> AboutAction.showAbout());
        debugItem.selectedProperty().addListener(e -> {
            WindowView.setDebugging(!WindowView.isDebugging());
            repaintMap();
        });


        fileMenu.getItems().addAll(importItem,loadItem,saveItem,quitItem);
        optMenu.getItems().addAll(debugItem,prefItem);
        helpMenu.getItems().addAll(wikiItem,docItem,aboutItem);
        showMenu.getItems().addAll(
                showFpsBtn, showPoisBtn
        );

        changeItem.getItems().addAll(
                mapTheme,customThemeBtn
        );

        this.getMenus().addAll(fileMenu, showMenu, optMenu, changeItem, helpMenu);
    }

    /**
     * Repaint the map.
     */
    private void repaintMap() {
        MapView.getSwingView().repaint();
    }

    /**
     * Open url.
     * @param url Uses the url to check browser.
     */
    private void openUrl(String url){
        String[] browsers = {"chrome.exe","firefox.exe","iexplore"};
        int browserIndex = 0;
        try{
            checkBrowser(browsers,browserIndex,url);
        }catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * CheckBrowser.
     * @param browsers Uses the array of browsers to start and check browser together with url.
     * @param i Uses int i to find the index in the array.
     * @param url Uses the url to start and check browsers.
     * @throws IOException If the method doesn't work as wanted.
     */
    private void checkBrowser(String[] browsers,int i, String url) throws IOException{
        Process p = startBrowser(browsers[i],url);
        if(p.getErrorStream().read() == 84){
            if(i+1 > browsers.length-1) return;
            checkBrowser(browsers,i+1,url);
        }
    }

    /**
     * Starts the browser with the help of commandline.
     * @param browser Uses the browser to open the browser.
     * @param url Uses the url to open the website.
     * @return Returns the browser with the website.
     * @throws IOException If the method doesn't work as wanted.
     */
    private Process startBrowser(String browser, String url) throws IOException{
        return Runtime.getRuntime().exec("cmd" + " " + "/c" + "start" + " " + browser + " " + url);
    }
}