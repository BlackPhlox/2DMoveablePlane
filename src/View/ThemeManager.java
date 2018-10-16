package View;

import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static View.WindowView.debugPrintln;

/**
 * The ThemeManager makes it possible to switch between themes.
 */
public class ThemeManager {
    private static Scene scene;
    static File programStyle = new File("stylesheets/main.css");
    private static File blueTheme = new File("stylesheets/bluetheme.css");
    private static File blackTheme = new File("stylesheets/blacktheme.css");
    private static File whiteTheme = new File("stylesheets/whitetheme.css");
    private static String dataFolderName = WindowView.getDataFolderName();
    private static String userDirectory = WindowView.getUserDirectory();

    /**
     * Construct the ThemeManager.
     * @param scene Uses the scene to set the scene of the ThemeManager.
     */
    public static void setScene(Scene scene) {
        ThemeManager.scene = scene;
    }

    /**
     * Checks if file exists.
     * @param filePath Uses the file path to create a new file.
     * @return Returns if the file exist.
     */
    public static boolean fileExist(String filePath){
        File file = new File(filePath);
        return file.exists() && !file.isDirectory();
    }

    /**
     * Loads the theme.
     * @param file Uses the file to get the path and the absolute path.
     */
    public static void loadTheme(File file, boolean embedded){
        debugPrintln("Loading theme: " + file.getName());
        String url = WindowView.getFileString(file);
        if(embedded) url = WindowView.getEmbeddedFileString(file);
        scene.getStylesheets().remove(url);
        scene.getStylesheets().add(url);
    }

    /**
     * Checks if the dir exist if not it makes the dir, if it does it returns false.
     * @param s Uses s as directory.
     * @return The method make directory or false.
     */
    public static boolean createDir(String s) { //The return statement is never used, but there for further development.
        String dir = s;
        String dirTag = "\\";
        if(WindowView.isJAR()) {
            dirTag = "/";
        }
        dir = userDirectory + dirTag + dir;
        if(!new File(dir).exists()) {
            debugPrintln("Creating directory: " + dir);
            return new File(dir).mkdir();
        }
        return false;
    }

    /**
     * Creates temporary theme.
     * @param c Used as the color for the temporary theme.
     */
    public static void createTempTheme(Color c){
        createTheme("temp.css",c);
    }

    /**
     * Creates default theme.
     * @param c Used as the color for the default theme.
     */
    public static void createDefaultTheme(Color c){
        createTheme( "DefaultTheme.css",c);
    }

    /**
     * Loads default.
     */
    static void loadDefault() {
        String dirTag = "\\";
        if(WindowView.isJAR()){
            dirTag = "/";
        }
        if(new File(dirTag+userDirectory + dirTag + dataFolderName + dirTag +"DefaultTheme.css").exists()) {
            debugPrintln("Loading default theme");
            loadTheme(new File(userDirectory + dirTag + dataFolderName + dirTag +"DefaultTheme.css"),false);
        }
    }

    /**
     * Create theme.
     * @param path Used as the path for which the color is saved as.
     * @param c Used as the color for the theme.
     */
    private static void createTheme(String path, Color c){
        scene.getStylesheets().clear();
        debugPrintln("Creating new uitheme " + path + " With the color: " + c);
        String dirTag = "\\";
        if(WindowView.isJAR()){
            dirTag = "/";
        }
        try{
            writeTheme(path,c);
            loadTheme(new File(dirTag+userDirectory+dirTag+dataFolderName+dirTag+path),false);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates custom named themes.
     * @param themeName Uses the name to save the theme with a customized name.
     * @param c Used as the color for the theme.
     * @throws IOException If the method doesn't function properly.
     */
    public static void createCustomNamedTheme(String themeName, Color c) throws IOException{
        debugPrintln("Creating new uitheme called " + themeName + " With the color: " + c);
        writeTheme(themeName+".css", c);
    }

    /**
     * Writes the theme.
     * @param fileName Uses the name to save the customized name.
     * @param c Used as the color for the theme.
     * @throws IOException If the method doesn't function properly.
     */
    private static void writeTheme(String fileName, Color c) throws IOException {
        createDir(dataFolderName);
        BufferedWriter bw;
        String dirTag = "\\";
        if(WindowView.isJAR()){
            dirTag = "/";
        }
        bw = new BufferedWriter(new FileWriter(userDirectory+dirTag+dataFolderName+dirTag+fileName, false));
        StringBuilder lines = new StringBuilder();
        if(WindowView.isJAR()){
            String dir = programStyle.getPath().replace("\\","/");
            Stream<String> stream = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(dir))).lines();
            stream.forEach(lines::append);
        } else {
            Files.lines(programStyle.toPath()).forEach(lines::append);
        }

        bw.write( lines +"\n"+
                "/* To customize the program further, go to https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html and write to this file */\n" +
                ".root {\n    " +
                "-fx-base: rgba(" +
                c.getRed()*255   + "," +
                c.getGreen()*255 + "," +
                c.getBlue()*255  + "," +
                c.getOpacity() +
                ");\n}"
        );
        bw.close();
    }

    /**
     * Deletes a file.
     * @param fileName The file which is to deleted.
     */
    static void deleteFile(String fileName){
        String dirTag = "\\";
        if(WindowView.isJAR()){
            dirTag = "/";
        }
        if(new File(userDirectory+dirTag+dataFolderName).exists()){
            File folder = new File(userDirectory+dirTag+dataFolderName);
            File file = new File(userDirectory+dirTag+dataFolderName+dirTag+fileName);
            delete(folder,file);
        }
    }

    /**
     * Delete a folder.
     * @param folder The folder which is to be deleted.
     * @param file To check if anymore files exists in the folder.
     */
    private static void delete(File folder, File file){
        if(folder.list().length <= 1){
            if(file.exists()) {
                deleteAll(folder);
            } else if (folder.list().length == 0){
                deleteAll(folder);
            }
        } else {
            if(file.exists()) {
                try {
                    if (WindowView.isJAR()) {
                        Files.delete(new File(file.getAbsolutePath().replace("\\", "/")).toPath());
                    } else {
                        Files.delete(file.toPath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Delete directory.
     * @param f The file which is to be deleted.
     */
    private static void deleteAll(File f)  {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteAll(c);
        }
    }

    /**
     * Get the blue theme.
     * @return The blueTheme.
     */
    public static File getBlueTheme() {
        return blueTheme;
    }

    /**
     * Get the black theme.
     * @return The blackTheme.
     */
    public static File getBlackTheme() {
        return blackTheme;
    }

    /**
     * Get the white theme.
     * @return The whiteTheme.
     */
    public static File getWhiteTheme() {
        return whiteTheme;
    }
}