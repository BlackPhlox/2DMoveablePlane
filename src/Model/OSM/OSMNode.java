package Model.OSM;

import java.io.Serializable;

/**
 * This class represents way segments on the map.
 */
public class OSMNode implements Serializable{
    private float lon, lat;
    private int arrayId;

    /**
     * Constructor for OSMNode.
     * @param lon Longitude (x coordinate).
     * @param lat Latitude (y coordinate).
     */
    public OSMNode(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * Returns longitude of this node.
     * @return longitude.
     */
    public float getLon() {
        return lon;
    }

    /**
     * Returns latitude of this node.
     * @return Latitude.
     */
    public float getLat() {
        return lat;
    }


    /**
     * Sets the array id.
     * @param arrayId ArrayId.
     */
    public void setArrayId(int arrayId) {
        this.arrayId = arrayId;
    }

    /**
     * Returns the array id.
     * @return ArrayId.
     */
    public int getArrayId() {
        return arrayId;
    }

}
