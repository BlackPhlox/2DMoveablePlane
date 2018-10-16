package Model.OSM;


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class describes the NamedShapes, that is a class that wraps every Shape to contain a OSMWay
 */
public class NamedShapes implements Shape, Serializable {
    private String name;
    private Shape shape;
    private List<Point> points;

    /**
     * Nested class creates point, and the functionality which is needed.
     */
    public class Point implements Serializable {
        float lat, lon;
        int id;
        private Point(double lat, double lon, int id) {
            this.lat = (float) lat;
            this.lon = (float) lon;
            this.id = id;
        }

        public float getLat() {
            return lat;
        }

        public float getLon() {
            return lon;
        }

        public int getId() {
            return id;
        }
    }

    /**
     * Constructor for namedShapes.
     * @param shape Shape used for setting the shape.
     * @param name Name of the shape.
     */
    public NamedShapes(Shape shape, String name) {
        this.shape = shape;
        this.name = name;
    }

    public void addPoint(double lat, double lon, int id){
        if(points==null)points = new ArrayList<>();
        points.add(new Point(lat, lon, id));
    }

    public List<Point> getPoints() {
        return points;
    }

    /**
     * Get bounds.
     * @return Shape's bounds.
     */
    @Override
    public Rectangle getBounds() {
        return shape.getBounds();
    }

    /**
     * Get bounds 2D.
     * @return Shape's bounds 2D.
     */
    @Override
    public Rectangle2D getBounds2D(){
        return shape.getBounds2D();
    }

    /**
     * Check if anything is in it.
     * @param x Uses the x value to check shape.contains.
     * @param y Uses the y value to check shape.contains.
     * @return Gives a boolean.
     */
    @Override
    public boolean contains(double x, double y) {
        return shape.contains(x,y);
    }

    /**
     * Check if anything is in it.
     * @param p Uses the specific point to check if shape.contains.
     * @return Gives a boolean.
     */
    @Override
    public boolean contains(Point2D p) {
        return shape.contains(p);
    }

    /**
     * Checks if intersects.
     * @param x Uses x as placement point.
     * @param y Uses y as placement point.
     * @param w Uses width as the width for the intersection.
     * @param h Uses height as the height for the intersection.
     * @return Gives a boolean.
     */
    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return shape.intersects(x,y,w,h);
    }

    /**
     * Get name.
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * This method checks if the shape intersects with the view
     * @param viewRect The view
     * @return True if it intersects
     */
    @Override
    public boolean intersects(Rectangle2D viewRect) {
        return shape.intersects(viewRect);
    }

    /**
     * Check if anything is in it.
     * @param x Uses x as placement point.
     * @param y Uses y as placement point.
     * @param w Uses width as the width for the shape.contains.
     * @param h Uses height as the height for the shape.contains.
     * @return Gives a boolean.
     */
    @Override
    public boolean contains(double x, double y, double w, double h) {
        return shape.contains(x,y,w,h);
    }

    /**
     * Check if anything is in it.
     * @param r Uses a rectangle to check if shape.contains.
     * @return Gives a boolean.
     */
    @Override
    public boolean contains(Rectangle2D r) {
        return shape.contains(r);
    }

    /**
     * Get the path iterator.
     * @param at AffineTransform.
     * @return The shapes path iterator.
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at){
            return shape.getPathIterator(at);
    }

    /**
     * Get the path iterator.
     * @param at AffineTransform.
     * @param flatness The flatness of the path iterator.
     * @return The shapes path iterator.
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return shape.getPathIterator(at,flatness);
    }
}