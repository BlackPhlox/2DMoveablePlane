package Controller.UIAction;

public class StringManipulation{
    /**
     * Changes the String to proper case.
     *
     * C. Ross 2009, Edited by Alan Moore
     * https://goo.gl/UTSVjN
     * @param s Take a string like "sTriNG"
     * @return The string with proper case like "String"
     */
    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
}
