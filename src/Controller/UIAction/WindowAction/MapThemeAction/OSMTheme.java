package Controller.UIAction.WindowAction.MapThemeAction;

import Model.OSM.OSMWayType;

import java.util.Map;
import java.util.TreeMap;

/**
 * Uses the colors from OSMColor to create the theme.
 */
public class OSMTheme{
    private String themeName;
    private Map<OSMWayType,OSMColor> theme = new TreeMap<>();

    /**
     * Uses OSMColor to place the colors onto the OSMWayTypes.
     * @param themeName The themeName of the theme the user wants to change to.
     */
    OSMTheme(String themeName){
        this.themeName = themeName;
        for(int i = 0; i < OSMWayType.values().length; i++){
            if(OSMWayType.values()[i].getColor()== null) continue;
            theme.put(OSMWayType.values()[i], new OSMColor(OSMWayType.values()[i]));
        }
    }

    /**
     * Set the color.
     * @param osmColor Uses the colors to create the theme.
     */
    public void setOSMColor(OSMColor osmColor){
        theme.put(osmColor.getOsmType(),osmColor);
    }

    /**
     * Get the theme.
     * @return The theme returns as the type Map between OSMWayType and OSMColor.
     */
    public Map<OSMWayType, OSMColor> getTheme() {
        return theme;
    }

    /**
     * Makes the theme to a String.
     * @return The themeName is returned as a String.
     */
    @Override
    public String toString() {
        return themeName;
    }
}
