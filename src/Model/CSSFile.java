package Model;

import java.io.File;

/**
 *  CSS class for use in UI
 */

public class CSSFile extends File {
    public CSSFile(String pathname) {
        super(pathname);
    }

    /**
     * String representation of CSSFile object.
     * @return String representation of CSSFile object.
     */
    @Override
    public String toString() {
        return getName().substring(0,getName().length()-4);
    }

    /**
     * Checks if File f is .css.
     * @param f File to check.
     * @return CSSFile.
     */
    public static CSSFile valueOf(File f){
        if(
            f.getName().endsWith(".css") &&
            f.getName().matches("^\\s*\\S+\\s*$" //Does not contain whitespaces
            )){
            return new CSSFile(f.getPath());
        } else {
            return null;
        }
    }
}
