package Model.OSM;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Representation of a road.
 */
public class OSMWay extends ArrayList<OSMNode> implements Serializable{

    /**
     * Constructor of OSMWay.
     */
    public OSMWay(){ }



    /**
     * Returns the from node of this way.
     * @return OSMNode from.
     */
    public OSMNode from() {
        return get(0);
    }

    /**
     * Returns the to node of this way.
     * @return OSMNode to.
     */
    public OSMNode to() {
        return get(size()-1);
    }

    }
