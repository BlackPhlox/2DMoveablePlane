package Model.Graph;

import Model.OSM.*;

import java.io.Serializable;
import java.util.*;
import java.util.List;

/**
 * Digraph for handling pathfinding
 */
public class EdgeWeightedDigraph implements Serializable {
    private static EdgeWeightedDigraph instance;
    private static List<DirectedEdge>[] adj;
    private static int size;

    /**
     * Creates the EdgeWeighted Digraph
     */
    private EdgeWeightedDigraph() {}

    /**
     * Sets the size of the EdgeWeightedDigraph
     * @param size Size to set
     */
    public static void setSize(int size) {
        EdgeWeightedDigraph.size = size;
    }

    /**
     * Gets the EdgeWeightedDigraph instance
     * @return EdgeWeightedDigraph instance
     */
    public static EdgeWeightedDigraph getInstance() {
        if (instance == null) instance = new EdgeWeightedDigraph();
        return instance;
    }

    /**
     * Sets the adjacent array of lists of edges that start in specific noded according to arrayId
     * @param adj List
     */
    public static void setAdj(List<DirectedEdge>[] adj) {
        EdgeWeightedDigraph.adj = adj;
    }

    /**
     * Returns the adjacent list
     * @return Adj
     */
    public List<DirectedEdge>[] getAdj() {
        return adj;
    }

    /**
     * Adds an edge to the adj array of lists of directedEdges
     * @param from The node the edge starts from
     * @param to The node the edge ends at
     * @param shortestWeight The shortest weight in km
     * @param speed Speed in km/h
     * @param walkAllowed Check if walk is allowed
     * @param bikeAllowed Check if bike is allowed
     * @param carAllowed Check if car is allowed
     * @param roadName The roadname in the edge
     */
    public static void addEdge(OSMNode from,
                               OSMNode to,
                               float shortestWeight,
                               short speed,
                               boolean walkAllowed,
                               boolean bikeAllowed,
                               boolean carAllowed,
                               String roadName) {
        DirectedEdge edge = new DirectedEdge(from.getArrayId(),
                to.getArrayId(),
                new float[]{from.getLon(), from.getLat()},
                shortestWeight,
                speed,
                walkAllowed,
                bikeAllowed,
                carAllowed,
                roadName);
        if(adj == null) {
            adj  = (ArrayList<DirectedEdge>[]) new ArrayList[size];
        }
        if(adj[from.getArrayId()]==null) adj[from.getArrayId()] = new ArrayList<>();
        adj[from.getArrayId()].add(edge);
    }

    /**
     * Adds a bi-directional edge to the adj array of lists of directedEdges, by calling addEdge twice and swapping
     * to and from node
     * @param from The node the edge starts from
     * @param to The node the edge ends at
     * @param shortestWeight The shortest weight in km
     * @param speed Speed in km/h
     * @param walkAllowed Check if walk is allowed
     * @param bikeAllowed Check if bike is allowed
     * @param carAllowed Check if car is allowed
     * @param roadName The roadname in the edge
     */
    public static void addBidirectionalEdge(OSMNode from,
                                            OSMNode to,

                                            float shortestWeight,
                                            short speed,
                                            boolean walkAllowed,
                                            boolean bikeAllowed,
                                            boolean carAllowed,
                                            String roadName) {
        addEdge(from, to, shortestWeight, speed, walkAllowed, bikeAllowed, carAllowed, roadName);
        addEdge(to, from, shortestWeight, speed, walkAllowed, bikeAllowed, carAllowed, roadName);
    }
}