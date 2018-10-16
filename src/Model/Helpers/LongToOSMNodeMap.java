package Model.Helpers;

import Model.OSM.OSMNode;

import java.io.Serializable;

/**
 * Specialised HashMap for OSM-Nodes that reduces overhead
 * and minimized memory-usage.
 */
public class LongToOSMNodeMap implements Serializable {
    private Node[] table;
    private int MASK;

    /**
     * Constructor for LongToOSMNodeMap
     * @param capacity For map
     */
    public LongToOSMNodeMap(int capacity) {
        table = new Node[1 << capacity]; // there are 2^{capacity} table cells
        MASK = table.length - 1;
    }

    /**
     * Puts a node into the map
     * @param id Of node
     * @param lon X coordinate of node
     * @param lat Y coordinate of node
     */
    public void put(long id, double lon, double lat) {
        int position = Long.hashCode(id) & MASK;
        table[position] = new Node(id, lon, lat, table[position]);
    }

    /**
     * Sets the array index of the node in the adj array in EdgeWeightedDigraph
     * @param id Node id
     * @param arrayId Adj index
     */
    public void setArrayid(long id, int arrayId) {
        get(id).setArrayId(arrayId);
    }

    /**
     * Returns a node with a given id
     * @param id Of node
     * @return Node of specified id
     */
    public Node get(long id) {
        int position = Long.hashCode(id) & MASK;
        for (Node n = table[position]; n != null; n = n.next) {
            if (n.id == id) {
                return n;
            }
        }
        return null;
    }

    /**
     * Nested class node
     */
    class Node extends OSMNode {
        long id;
        Node next;

        public Node(long id, double lon, double lat, Node n) {
            super((float) lon,(float) lat);
            this.id = id;
            this.next = n;
        }

    }
}
