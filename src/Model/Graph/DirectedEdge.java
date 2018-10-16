package Model.Graph;

import java.io.Serializable;

/**
 * Representation of a directed edge that connects two OSMNodes
 */
public class DirectedEdge implements Serializable {
    private int fromId, toId;
    private float lon;
    private float lat;
    private boolean walkAllowed, bikeAllowed, carAllowed;
    private String roadName;
    private float shortestWeight;
    private short speed;

    /**
     * Constructor for DirectedEdge
     * @param from from node
     * @param shortestWeight weight
     * @param speed max speed
     */
    DirectedEdge(int fromId,
                 int toId,
                 float[] from,
                 float shortestWeight,
                 short speed,
                 boolean walkAllowed,
                 boolean bikeAllowed,
                 boolean carAllowed,
                 String roadName) {
        this.fromId = fromId;
        this.toId = toId;
        this.lat = from[0];
        this.lon = from[1];
        this.shortestWeight = shortestWeight;
        this.speed = speed;
        this.bikeAllowed=bikeAllowed;
        this.walkAllowed=walkAllowed;
        this.carAllowed=carAllowed;
        this.roadName=roadName;

    }

    /**
     * Get road name.
     * @return The roadName.
     */
    public String getRoadName() {
        return roadName;
    }

    /**
     * Is Bika allowed.
     * @return BikeAllowed.
     */
    public boolean isBikeAllowed() {
        return bikeAllowed;
    }

    /**
     * Is car allowed.
     * @return CarAllowed.
     */
    public boolean isCarAllowed() {
        return carAllowed;
    }

    /**
     * Is walk allowed.
     * @return WarAllowed.
     */
    public boolean isWalkAllowed() {
        return walkAllowed;
    }

    /**
     * Get from id.
     * @return FromId.
     */
    public int getFromId() {
        return fromId;
    }

    /**
     * Get to id.
     * @return ToId.
     */
    public int getToId() {
        return toId;
    }

    /**
     * Get from.
     * @return Float array with latitude and longitude.
     */
    public float[] getFrom() {
        return new float[]{lat,lon};
    }

    /**
     * Gets the shortest weight
     * @return ShortestWeight
     */
    public float getShortestWeight() {
        return shortestWeight;
    }

    /**
     * Gets the fastest weight
     * @return ShortestWeight/speed
     */
    public float getFastestWeight() {
        return shortestWeight/speed;
    }
}