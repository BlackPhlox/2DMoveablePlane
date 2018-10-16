package KdTree;


import Model.OSM.NamedShapes;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class defines a KdTree, where every OSMWayType shape is located.
 * It is used to limit the amount of shapes drawn, so only shapes in the view are shown.
 */
public class KdTree implements Serializable {

    private Node root = new Node();

    /**
     * Creates the KdTree
     */
    KdTree(){
    }


    /**
     * The method returns all the nodes as a list in the KdTree
     * @return A list of all nodes
     */
    public ArrayList<Node> get() {
        return new ArrayList<>(getNodes(root));
    }

    /**
     * This method recursively adds all the children nodes of the parameter node to a list
     * @param node The parent node of all the nodes that will be added to the list
     * @return The list of children nodes of the parameter node
     */
    private ArrayList<Node> getNodes(Node node){
        ArrayList<Node> list = new ArrayList<Node>();
        if(node.getLeftChild()!=null) {
            list.addAll(getNodes(node.getLeftChild()));
        }
        if(node.getRightChild()!=null){
            list.addAll(getNodes(node.getRightChild()));
        }
        list.add(node);
        return list;

    }

    /**
     * This method finds the shapes in the given focus area by starting on the root node of the KdTree and
     * recursively comparing the children nodes with the parameter boundaries.
     * @param left The left boundary of the focus area in view
     * @param bottom The bottom boundary of the focus area in view
     * @param right The right boundary of the focus area in view
     * @param top The top boundary of the focus area in view
     * @return The set of shapes in the focus area
     */
    public Set<Shape> rangeSearch(double left, double bottom, double right, double top){
        if(left > right || top > bottom){
            throw new InvalidParameterException("Not a valid rectangle");
        }
        return new HashSet<>(rangeSearch(root,left,bottom,right,top));
    }

    /**
     * This method returns the set of shapes in the given focus area by recursively finding
     * the set of shapes of the children nodes and comparing with the parameter boundaries
     * @param node The parent node of the set of shapes
     * @param left The left side boundary of the focus area
     * @param bottom The bottom boundary of the focus area
     * @param right The right boundary of the focus area
     * @param top The top boundary of the focus area
     * @return The set of shapes
     */
    private Set<Shape> rangeSearch(Node node, double left, double bottom, double right, double top){
        Set<Shape> result = new HashSet<>();
        if(node.getLeftChild() != null || node.getRightChild() != null){
            if(node.isVertical()){
                if(left <= node.getValue()){
                    result.addAll(rangeSearch(node.getLeftChild(),left,bottom,right,top));
                }
                if(node.getValue() <= right){
                    result.addAll(rangeSearch(node.getRightChild(),left,bottom,right,top));
                }

            } else {
                if(top <= node.getValue()){
                    result.addAll(rangeSearch(node.getLeftChild(),left,bottom,right,top));
                }
                if(node.getValue() <= bottom){
                    result.addAll(rangeSearch(node.getRightChild(),left,bottom,right,top));
                }
            }
        } else {
            Leaf end = (Leaf)node;
            return end.getLeaf();
        }


        return result;
    }

    /**
     * This method returns the root of the KdTree
     * @return The root
     */
    public Node getRoot() {
        return root;
    }

    /**
     * This methods finds the shape closest to the mouseposition on the view
     * @param mousePos The location of the mouse using coordinates
     * @param left The left boundary of the view
     * @param bottom The bottom boundary of the view
     * @param right The right boundary of the view
     * @param top The top boundary of the view
     * @return The closest shape
     */
    public NamedShapes NearestNeighbour(Point2D mousePos, double left, double bottom, double right, double top) {
        Set<Shape> shapes = rangeSearch(left,bottom,right,top);
        NamedShapes closestShape = null;
        double shortestDist = Double.POSITIVE_INFINITY;
        for(Shape shape: shapes){
            if(shape instanceof NamedShapes) {
                NamedShapes namedShapes = (NamedShapes)shape;
                if(namedShapes.getName()!=null){
                    double x = shape.getBounds2D().getCenterX();
                    double y = shape.getBounds2D().getCenterY();
                    double currentDist = Math.sqrt(Math.pow(Math.abs(mousePos.getY() - y), 2.0) + Math.pow(Math.abs(mousePos.getX() - x), 2.0));
                    if (currentDist < shortestDist) {
                        shortestDist = currentDist;
                        closestShape = namedShapes;
                    }
                }
            }
        }
        return closestShape;

    }


    /**
     * This method finds OSMNode closest to the specific location, given by coordinates
     * @param lon The longitude position
     * @param lat The latitude position
     * @return The closest node
     */
    public Integer NearestNeighbourNode(float lon, float lat) {
        float left = lon - 2;
        float right = lon + 2;
        float bottom = lat +2;
        float top = lat-2;

        Set<Shape> shapes = rangeSearch(left,bottom,right,top);
        Integer closestNode = null;
        double shortestDist = Double.POSITIVE_INFINITY;
        for(Shape shape: shapes){
            if(shape instanceof NamedShapes){
                NamedShapes namedShapes = (NamedShapes)shape;
                List<NamedShapes.Point> points = namedShapes.getPoints();
                if(points!=null) {
                    for(NamedShapes.Point point: points) {
                        double x = point.getLon();
                        double y = point.getLat();
                        double currentDist = Math.abs(Math.sqrt
                                (Math.pow(
                                        (lat
                                                - y), 2.0)
                                        + Math.pow((lon - x), 2.0)));
                        if (currentDist < shortestDist) {
                            shortestDist = currentDist;
                            closestNode = point.getId();
                        }
                    }
                }
            }
        }
        return closestNode;
    }
}