package Controller.InputAction;

import Controller.UIAction.WindowAction.PreferenceAction;
import View.Content.MapContext.SwingView;
import Model.Model;
import Model.PointOfInterest;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.awt.event.*;
import java.awt.geom.Point2D;

import static java.lang.Math.pow;

/**
 * This class uses swing class MouseAdapter to listen for mouseActions on the swing canvas.
 */
public class MouseController extends MouseAdapter implements MouseListener{
    private static SwingView canvas;
    private static Point2D lastMousePosition;
    private static boolean AAToBeToggled;

    private static Point2D modelPos;
    private static Point2D screenPos;
    private static Point2D realPos;

    /**
     * Constructor for MouseController, makes the canvas.
     * Adds MouseWheelListener and MouseMotionListener.
     * @param c Takes c, as a SwingView, to use as canvas.
     */
    public MouseController(SwingView c) {
        canvas = c;
        canvas.addMouseListener(this);
        canvas.addMouseWheelListener(this);
        canvas.addMouseMotionListener(this);
    }

    /**
     * Disable antialiasing and pans on the canvas, when mouse is dragged.
     * @param e MouseEvent to get currentPosition of the mouse, and make it last mouse position.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        Point2D currentMousePosition = e.getPoint();
        if(PreferenceAction.isAaWhenMoving()){
            temporarilyDisableAA();
        }
        double dx = currentMousePosition.getX() - lastMousePosition.getX();
        double dy = currentMousePosition.getY() - lastMousePosition.getY();
        canvas.pan(dx, dy);
        lastMousePosition = currentMousePosition;
    }

    /**
     * Enable antialiasing when mouse released.
     * @param e Uses mouse event to figure out, when mouse is released.
     */
    public void mouseReleased(MouseEvent e){
        if(PreferenceAction.isAa() && PreferenceAction.isAaWhenMoving()) enableAAIfTemporarilyDisabled();
    }

    /**
     * Make point of interested, when mouse pressed.
     * @param e Uses to get last mouse position.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        lastMousePosition = e.getPoint();
        canvas.requestFocus();
        if(e.isShiftDown()){
            Point2D pos = canvas.toModelCoords(MouseController.getLastMousePosition());
            Model.getInstance().addPOI(new PointOfInterest(pos.getX(),pos.getY()));
            canvas.repaint();
        }
    }

    /**
     * Get model coordinates when mouse is moved.
     * @param e Uses mouse event to know when mouse is moved.
     */
    public void mouseMoved(MouseEvent e) {
        if(PreferenceAction.isAa()) canvas.setUseAntiAliasing(true);
        lastMousePosition = new Point2D.Double(e.getX(),e.getY());
        modelPos = canvas.toModelCoords(e.getPoint());
        screenPos = new Point2D.Double(e.getX(),e.getY());
        realPos = new Point2D.Double(modelPos.getX()*Model.getLonFactor(),-modelPos.getY());
        if(canvas.getCoordinateType() != 0) canvas.repaint();
    }

    /**
     * Get the last mouse position.
     * @return Last mouse position.
     */
    public static Point2D getLastMousePosition() {
        return lastMousePosition;
    }

    /**
     * A method which prevents the user from zooming beyond the bounds of the map.
     * @param point2D the point which may or may-not be out of the bounds of the map.
     * @param model Uses model to get edges.
     * @param canvas Uses canvas to set location.
     * @return An adapted point of zoom that that do not go over the map bounds.
     */
    private Point2D zoomLimiter(Point2D point2D, Model model, SwingView canvas){
        Point2D zoomPoint = canvas.toModelCoords(point2D);
        boolean xMaxEdge = !(model.getMaxLon()>zoomPoint.getX());
        boolean xMinEdge = !(model.getMinLon()<zoomPoint.getX());
        boolean yMaxEdge = !(model.getMaxLat()<zoomPoint.getY());
        boolean yMinEdge = !(model.getMinLat()>zoomPoint.getY());
        if(xMaxEdge) point2D.setLocation(canvas.toScreenCoords(new Point2D.Double(model.getMaxLon(),zoomPoint.getY())));
        if(xMinEdge) point2D.setLocation(canvas.toScreenCoords(new Point2D.Double(model.getMinLon(),zoomPoint.getY())));
        if(yMaxEdge) point2D.setLocation(canvas.toScreenCoords(new Point2D.Double(zoomPoint.getX(),model.getMaxLat())));
        if(yMinEdge) point2D.setLocation(canvas.toScreenCoords(new Point2D.Double(zoomPoint.getX(),model.getMinLat())));
        if(xMaxEdge && yMaxEdge) point2D.setLocation(canvas.toScreenCoords(new Point2D.Double(model.getMaxLon(),model.getMaxLat())));
        if(xMaxEdge && yMinEdge) point2D.setLocation(canvas.toScreenCoords(new Point2D.Double(model.getMaxLon(),model.getMinLat())));
        if(xMinEdge && yMinEdge) point2D.setLocation(canvas.toScreenCoords(new Point2D.Double(model.getMinLon(),model.getMinLat())));
        if(xMinEdge && yMaxEdge) point2D.setLocation(canvas.toScreenCoords(new Point2D.Double(model.getMinLon(),model.getMaxLat())));
        return point2D;
    }

    /**
     * Mouse wheel moved is used for creating an animation timer, when scrolling (zooming), which uses an delay after mouse wheel stopped.
     * So that it gets a smooth zoom effect.
     * @param e Uses mouse wheel event to know when the mouse wheel is used.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Point2D screenPointZoom = zoomLimiter(e.getPoint(),Model.getInstance(),canvas);
        if(PreferenceAction.isAaWhenMoving()) temporarilyDisableAA();
        if(PreferenceAction.isSmoothZoom()){
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    canvas.smoothZoom(-e.getWheelRotation()*0.5, -screenPointZoom.getX(), -screenPointZoom.getY());
                }
            };
            timer.start();
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.millis(500),
                    ae -> {timer.stop();
                        if(PreferenceAction.isAa())canvas.setUseAntiAliasing(true);
                    }));
            timeline.play();
        } else {
            double factor = pow(1.1, -e.getWheelRotation()*0.5);
            canvas.zoom(factor, -screenPointZoom.getX(), -screenPointZoom.getY());
        }
    }

    /**
     * Temporarily disable anti aliasing.
     */
    public static void temporarilyDisableAA() {
        if(canvas.isUseAntiAliasing()) {
            canvas.toggleAntiAliasing();
            AAToBeToggled = true;
        }
    }

    /**
     * Enable antialiasing if temporarily disabled.
     */
    private static void enableAAIfTemporarilyDisabled() {
        if(AAToBeToggled) {
            canvas.toggleAntiAliasing();
            AAToBeToggled = false;
        }
    }

    /**
     * Get the model position.
     * @return The point 2D modelPos.
     */
    public static Point2D getModelPos() {
        return modelPos;
    }

    /**
     * Get the screen position.
     * @return The point 2D screenPos.
     */
    public static Point2D getScreenPos() {
        return screenPos;
    }

    /**
     * Get the real position.
     * @return The point 2D realPos.
     */
    public static Point2D getRealPos() {
        return realPos;
    }
}
