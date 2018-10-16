package Model;

import Controller.UIAction.WindowAction.AlertTemplate;
import Model.Graph.DirectedEdge;
import Model.Graph.EdgeWeightedDigraph;
import Model.Helpers.ArrayIdWrapper;
import Model.OSM.OSMAddress;
import Model.OSM.OSMNode;
import Model.OSM.OSMWayType;
import View.Content.Components.PathView;
import View.Content.MapContext.MapView;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class handles the route searching
 * to find the optimal route for a given range of parameters.
 */
public class Pathfinding {
    private static HashMap<Integer,Float> distTo;
    private static HashMap<Integer,Integer> predecessors;
    private String transport;
    private String route;
    private PriorityQueue<ArrayIdWrapper> minPQ = new PriorityQueue<>();
    private static Pathfinding instance;
    private EdgeWeightedDigraph edgeWeightedDigraph;

    /**
     * Returns the instance of this class.
     * @return Pathfinding instance.
     */
    private static Pathfinding getInstance(){
        if(instance == null) instance = new Pathfinding();
        return instance;
    }

    /**
     * Setup for findPath() method.
     * @param transport Type of transport (walk, bicycle, car).
     * @param route Type of route (shortest, fastest).
     * @param from Current position.
     * @param to End position.
     * @param breakPoint Optional breakpoint.
     */
    public static void routeSearch(String transport, String route, OSMAddress from, OSMAddress to, OSMAddress breakPoint) {
        Pathfinding p = Pathfinding.getInstance();
        p.setTransport(transport);
        p.setRoute(route);
        p.setEdgeWeightedDigraph(EdgeWeightedDigraph.getInstance());
        p.resetNodeDistances();
        p.resetPredecessors();

        Model.getInstance().emptyPaths();

        p.findPath(from, to, breakPoint, transport, route);
        p.resetNodeDistances();
        p.resetPredecessors();
        MapView.getSwingView().repaint();
    }

    /**
     * Calculates the weight of adjacent nodes.
     * @param node Node to relax.
     */
    private void relax(int node) {
        List<DirectedEdge> adjList = edgeWeightedDigraph.getAdj()[node];
        DirectedEdge[] sortedArray = sort(adjList);
        if (sortedArray == null) return;
        for (DirectedEdge e : sortedArray) {
            if (transport.equals("Bike") && !e.isBikeAllowed()) continue;
            if (transport.equals("Walk") && !e.isWalkAllowed()) continue;
            if (transport.equals("Car") && !e.isCarAllowed()) continue;

            int toNodeId = e.getToId();
            float toNodeWeight = nodeWeight(toNodeId);
            float fromNodeWeight = nodeWeight(node);

            float edgeWeight;

            if (route.equals("Shortest")){
                edgeWeight = e.getShortestWeight();
            }
            else edgeWeight = e.getFastestWeight();

            if (edgeWeight + fromNodeWeight < toNodeWeight) {
                distTo.put(toNodeId,fromNodeWeight + edgeWeight);
                predecessors.put(toNodeId,node);
                minPQ.add(new ArrayIdWrapper(toNodeId));
            }
        }
    }

    /**
     * Finds a path from start address to end address.
     * @param startAddress OSMAddress to start at.
     * @param endAddress OSMAddress to end at.
     * @param breakPointAddress Optional breakpoint to go through.
     * @param transport Type of transport (Walk, Bicycle, Car).
     * @param route Type of route (Shortest, Fastest).
     */
    public void findPath(OSMAddress startAddress, OSMAddress endAddress, OSMAddress breakPointAddress, String transport, String route) {
        if (startAddress == null || endAddress == null) {
            AlertTemplate.infoAlert("Address error", "One or more address fields are empty", "");
            return;
        }
        if (startAddress == endAddress) {
            AlertTemplate.infoAlert("Address error", "Start and end location cannot be the same", "");
            return;
        }
        if (breakPointAddress == startAddress || breakPointAddress == endAddress) {
            AlertTemplate.infoAlert("Breakpoint error", "Breakpoint cannot contain start- or endpoint", "");
            return;
        }
        resetPredecessors();
        resetNodeDistances();
        this.transport = transport;
        this.route = route;

        int start = nearestNeighbourNode(startAddress.getLon(), startAddress.getLat());
        int end = nearestNeighbourNode(endAddress.getLon(), endAddress.getLat());
        distTo.put(start,(float)0.0);
        relax(start);
        while (minPQ.size() != 0) relax(minPQ.poll().getArrayId());

        List<Integer> path = new ArrayList<>();

        Integer n = end;
        while (true) {
            if (n == null) {
                AlertTemplate.infoAlert("Pathfinding error", "No path was found", "");
                break;
            }
            if (n.equals(start)) {
                path.add(start);
                drawPath(path);
                getDirections(path, transport);
                break;
            }
            path.add(n);
            n = predecessors.get(n);
        }
    }

    private float[] idToCoords(int id){
        return edgeWeightedDigraph.getAdj()[id].get(0).getFrom();
    }

    /**
     * Resets all node distances such that a new route search may be created.
     */
    public void resetNodeDistances() {
        distTo = new HashMap<>();
    }
    public void resetPredecessors() {
        predecessors = new HashMap<>();
    }

    /**
     * Returns the distance from source
     * @param arrayId nodeid to check distance from
     * @return Node distance
     */
    private float nodeWeight(int arrayId){
        distTo.putIfAbsent(arrayId, Float.POSITIVE_INFINITY);
        return distTo.get(arrayId);
    }


    /**
     * Draws the path on the map.
     * @param path List<OSMNode> to draw.
     */
    private void drawPath(List<Integer> path) {
        Path2D pathLine = new Path2D.Float();
        pathLine.moveTo(idToCoords(path.get(0))[0], idToCoords(path.get(0))[1]);
        for(int i = 1; i < path.size(); i++){
            pathLine.lineTo(idToCoords(path.get(i))[0], idToCoords(path.get(i))[1]);
        }

        Model m = Model.getInstance();
        m.addPath(pathLine);
    }

    /**
     * Finds the angle between two nodes.
     * @param from Node from.
     * @param to Node to.
     * @return Degree between two nodes.
     */
    private float tanDegrees(float[] from, float[] to){
        float tanDegrees = (float) Math.atan2(-(to[1]-from[1]),((to[0]-from[0])*180/Math.PI));
        if(tanDegrees < 0) return tanDegrees + 360;
        else return tanDegrees;
    }

    /**
     * Calculates the directions to guide the user.
     * @param path Type of path (Shortest, Fastest).
     * @param transport Type of transpot (Walk, Bicycle, Car).
     */
    private void getDirections(List<Integer> path, String transport) {
        int prevId = path.get(path.size() - 1);
        float[] prevNode = idToCoords(prevId);
        if(path.size() < 3) {
            PathView.addDirection(prevNode, "You can see the destination", DirectionType.END);
            PathView.setBtnWidthSubtractionValue(18);
            return;
        }
        float prevDegree = tanDegrees(prevNode, idToCoords(path.get(path.size() - 2)));
        DirectedEdge startEdge = findEdge(prevId, path.get(path.size() - 2));
        String prevRoadName = startEdge.getRoadName();
        float distanceSameRoad = Math.round(startEdge.getShortestWeight() * 1000);
        float totalDistance = distanceSameRoad;
        float totalTime;
        float speed = 1;
        if(transport.equals("Walk")) {
            //5 km/h
            speed = 5;
            totalTime = startEdge.getShortestWeight()/speed;
        } else if (transport.equals("Bike")) {
            speed = 15;
            totalTime = startEdge.getShortestWeight()/speed;
        } else {
            // time in hours
            totalTime = startEdge.getFastestWeight();
        }
        for (int i = path.size() - 2; i > 0; i--) {
            int currentNode = path.get(i);
            DirectedEdge currentEdge = findEdge(currentNode, path.get(i - 1));
            float distance = Math.round(currentEdge.getShortestWeight() * 1000);
            //time in hours
            float time;
            switch (transport) {
                case "Walk":
                    time = startEdge.getShortestWeight() / speed;
                    break;
                case "Bike":
                    time = startEdge.getShortestWeight() / speed;
                    break;
                default:
                    time = currentEdge.getFastestWeight();
                    break;
            }
            totalTime += time;
            totalDistance += distance;
            String currentRoadName = currentEdge.getRoadName();
            float currentDegree = tanDegrees(idToCoords(currentNode), idToCoords(path.get(i - 1)));
            if (prevRoadName == null && currentRoadName == null || prevRoadName != null && currentRoadName != null && prevRoadName.equals(currentRoadName)) {
                float deltaDegree = currentDegree - prevDegree;
                if (deltaDegree < 30 && deltaDegree > -30 || deltaDegree < 30 && deltaDegree > 0 || deltaDegree > 330 && deltaDegree < 360) {
                    distanceSameRoad += distance;
                }
            } else {
                if (i == 1) {
                    DirectedEdge endEdge = findEdge(path.get(1), path.get(0));
                    currentDegree = tanDegrees(idToCoords(path.get(1)), idToCoords(path.get(0)));
                    String endRoadName = endEdge.getRoadName();
                    printDirections(idToCoords(path.get(1)), currentDegree, prevDegree, endRoadName, distanceSameRoad, false);
                } else {
                    printDirections(idToCoords(currentNode), currentDegree, prevDegree, currentRoadName, distanceSameRoad, false);
                    distanceSameRoad = Math.round(currentEdge.getShortestWeight() * 1000);
                }
            }
            prevDegree = currentDegree;
            prevRoadName = currentRoadName;
        }

        printDirections(idToCoords(path.get(0)), 0, 0, null, distanceSameRoad, true);
        System.out.println("Total distance " + totalDistance);
        int hours = (int) Math.floor(totalTime);
        int minutes = Math.round(((totalTime - hours)*60));
        System.out.println("Total time: " + hours + " hours and " + minutes + " minutes");
        PathView.pathStatus(hours,minutes,totalDistance);

        if(PathView.getInstance().getItems().size() > 5){
            PathView.setBtnWidthSubtractionValue(30);
        } else {
            PathView.setBtnWidthSubtractionValue(18);
        }
        distTo = null;
    }

    /**
     * Find edge.
     * @param fromNodeId From nodeid.
     * @param toNodeId To nodeid.
     * @return The edge.
     */
    private DirectedEdge findEdge(int fromNodeId, int toNodeId) {
        List adjList = edgeWeightedDigraph.getAdj()[fromNodeId];
        for(Object e: adjList) {
            DirectedEdge edge = (DirectedEdge) e;
            if(edge.getFromId() == fromNodeId && edge.getToId() == toNodeId) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Print directions.
     * @param prevNode Previous node.
     * @param currentDegree Current node.
     * @param prevDegree Previous degree.
     * @param roadName Road name.
     * @param distance Distance.
     * @param end End.
     */
    private void printDirections(float[] prevNode, float currentDegree, float prevDegree, String roadName, float distance, boolean end){
        float deltaDegree = currentDegree - prevDegree;
        String unit = " m";
        if(distance > 1000) {
            distance = distance/1000;
            unit = " km";
        }
        if (roadName == null) roadName = "the next road";
        if(end) {
            PathView.addDirection(prevNode, "You will reach your destination in " + distance + unit, DirectionType.END);
        }else if(roadName.contains(" - ")) {
            String destinations[] = roadName.split("-");
            PathView.addDirection(prevNode, "Get on the ferry from " + destinations[0] + "to" + destinations[1], DirectionType.AHEAD);
        } else if(deltaDegree < 30 && deltaDegree > -30 || deltaDegree < 30 && deltaDegree > 0 || deltaDegree > 330 && deltaDegree < 360) {
            PathView.addDirection(prevNode, "Continue straight for "+ distance + unit + " on " + roadName, DirectionType.AHEAD);
        } else if(deltaDegree < -30 && deltaDegree > -150 || deltaDegree < 330 && deltaDegree > 210){
            PathView.addDirection(prevNode, "Turn right at " + roadName + " after " + distance + unit, DirectionType.TURN_RIGHT);
        } else {
            PathView.addDirection(prevNode, "Turn left at " + roadName + " after "+ distance + unit, DirectionType.TURN_LEFT);
        }
    }

    /**
     * Sorting of directed edges.
     * @param list Uses list to be sorted.
     * @return The array after sorting the list.
     */
    private DirectedEdge[] sort(List<DirectedEdge> list) {
        if (list == null) return null;
        DirectedEdge arr[] = new DirectedEdge[list.size()];
        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);

        for (int i = 1; i < arr.length; ++i)
        {
            float key;
            float comparator;
            int j = i-1;
            if (transport.equals("Shortest")) {
                key = arr[i].getShortestWeight();
                comparator = arr[j].getShortestWeight();
            } else {
                key = arr[i].getFastestWeight();
                comparator = arr[j].getFastestWeight();
            }

            DirectedEdge n = arr[i];

            while (j >= 0 && comparator > key)
            {
                arr[j+1] = arr[j];
                j = j-1;
            }
            arr[j+1] = n;
        }
        return arr;
    }

    /**
     * Returns the arrayId of the closest node
     * @param lon the longitude position
     * @param lat the lattitude position
     * @return the arrayId of closest node
     */
    private int nearestNeighbourNode(float lon, float lat) {
        return Model.getInstance().getKdTreeEnumMap().get(OSMWayType.ROAD).NearestNeighbourNode(lon, lat);
    }

    /**
     * Get distance to.
     * @return The distTo.
     */
    public static HashMap<Integer,Float> getDistTo() {
        return distTo;
    }

    /**
     * Set route.
     * @param route Uses the route to set the route.
     */
    private void setRoute(String route) {
        this.route = route;
    }

    /**
     * Set transport.
     * @param transport Uses transport to set transport.
     */
    private void setTransport(String transport) {
        this.transport = transport;
    }

    /**
     * Set edge weighted digraph.
     * @param edgeWeightedDigraph Uses edge weighted digraph to edgeWeightedDigraph.
     */
    public void setEdgeWeightedDigraph(EdgeWeightedDigraph edgeWeightedDigraph) {
        this.edgeWeightedDigraph = edgeWeightedDigraph;
    }
}
