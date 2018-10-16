package View.Content.MapContext;

import Controller.InputAction.MouseController;
import KdTree.Node;
import Model.Model;
import Model.OSM.OSMNode;
import Model.OSM.OSMWayType;
import Model.PathType;
import Model.PointOfInterest;
import View.Content.Tabs.PoiTab;
import View.WindowView;
import javafx.scene.control.Tooltip;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Math.pow;

/**
 * This class is Swing Component that has the
 * responsibility of drawing the map to the program
 * and visualizing the loaded files from model.
 */
public class SwingView extends JComponent implements Observer{
    private final Model model;
    private Rectangle2D viewRect;
    private static boolean useAntiAliasing = true, showFPS = WindowView.isDebugging(), showKD = false, showCoordinates = true, showLOD = true, showPois = true, showScalebar = true, showToolTip = true;
    private static int coordinateType = 0;
    private AffineTransform transform = new AffineTransform();
    private double fps = 0.0;
    private double widthRatio;
    private int currentLod = 0; //0: Most detail 0<:Less detail
    private static OSMWayType kdtreeDebugType = OSMWayType.BUILDING;
    private static double debugViewportSizePct = 0.25;
    private static HashMap<PathType,OSMNode> markingMap = new HashMap<>();
    private static final int[] LOD = {18300, 9000, 3400, 350, 250, 100};
    private static Tooltip tooltip = new Tooltip();

    /**
     * Construct the SwingView getting an instance of the model.
     */
    public SwingView() {
        this.model = Model.getInstance();
        model.addObserver(this);
        Tooltip.install(MapView.getSwingNode(),tooltip);
    }

    /**
     * Fills out a shape.
     * @param g Uses graphics to set the paint color and fill the shape.
     * @param osmWayType Uses the OSMWayType, to find the color and the shape of the osmWayType.
     */
    private void fillShape(Graphics2D g, OSMWayType osmWayType) {
        if(Model.getInstance().getKdTreeEnumMap() == null) return;
        if (widthRatio > osmWayType.getDrawLevel()) {
            g.setPaint(osmWayType.getColor());
            for (Shape shape : model.get(osmWayType, viewRect.getX(), viewRect.getMaxY(), viewRect.getMaxX(), viewRect.getY())) {
                if(shape.intersects(viewRect) || (WindowView.isDebugging() && widthRatio>osmWayType.getDrawLevel())) {
                    g.fill(shape);
                }
            }
        }
    }

    /**
     * Fills out a shape with size range.
     * @param g Uses graphics to set the paint color and fill the shape.
     * @param osmWayType Uses the OSMWayType, to find the color and the shape of the osmWayType.
     */
    private void fillShapeWithSizeRange(Graphics2D g, OSMWayType osmWayType) {
        if(Model.getInstance().getKdTreeEnumMap() == null) return;
        if (widthRatio > osmWayType.getDrawLevel()) {
            g.setPaint(osmWayType.getColor());
            for (Shape shape : model.get(osmWayType, viewRect.getX(), viewRect.getMaxY(), viewRect.getMaxX(), viewRect.getY())) {
                if(     shape.getBounds2D().getWidth()>osmWayType.getSizeDrawLevel() &&
                        shape.getBounds2D().getHeight()>osmWayType.getSizeDrawLevel() &&
                        widthRatio>osmWayType.getDrawLevel())
                {
                    g.fill(shape);
                } else if(shape.intersects(viewRect) && widthRatio>osmWayType.getLowerDrawLevel()) {
                    g.fill(shape);
                }
            }
        }
    }

    /**
     * Draws the shape.
     * @param g Uses graphics to set the paint color and fill the shape.
     * @param osmWayType Uses the OSMWayType, to find the color and the shape of the osmWayType.
     */
    private void drawShape(Graphics2D g, OSMWayType osmWayType) {
        if(Model.getInstance().getKdTreeEnumMap() == null) return;
        if (widthRatio > osmWayType.getDrawLevel()) {
            g.setStroke(osmWayType.getStroke());
            g.setPaint(osmWayType.getColor());
            for (Shape shape : model.get(osmWayType, viewRect.getX(), viewRect.getMaxY(), viewRect.getMaxX(), viewRect.getY())) {
                if(shape.intersects(viewRect) || (WindowView.isDebugging() && widthRatio>osmWayType.getDrawLevel())) {
                    g.draw(shape);
                }
            }
        }
    }

    /**
     * Draws the path.
     * @param g Uses graphics to set the stroke and draw.
     */
    private void drawPath(Graphics2D g){
        g.setPaint(OSMWayType.PATH.getColor());
        if(model.getPath()!=null) {
            g.setStroke(OSMWayType.PATH.getStroke());
            g.draw(model.getPath());
        }
    }

    /**
     * Paints the map and everything on it.
     * @param _g Uses graphics to draw.
     */
    @Override
    public void paint(Graphics _g) {
        long t1 = 0;
        if(showFPS) {
            t1 = System.nanoTime();
        }
        Graphics2D g = (Graphics2D) _g;
        g.setStroke(new BasicStroke(Float.MIN_VALUE));

        viewRect = createRenderFrame();

        drawBackground(g);

        transformRenderFrame(g);

        widthRatio = Math.round(getWidth()/viewRect.getWidth());

        calculateCurrentLod();

        if (useAntiAliasing) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        drawOSMWayTypes(g);

        drawKDTree(g);

        if (WindowView.isDebugging()) {
            drawMapBounds(g);
        }

        showPointsOfInterest(g);

        showMapMarkings(g);

        showNearestRoadName(g,WindowView.isDebugging());
        drawFPS(g,t1);
        if(WindowView.isDebugging()) drawLOD(g);
        drawCoordinates(g);
        if(!WindowView.isDebugging()) drawScaleBar(g);
    }

    /**
     * Draws the background.
     * @param g  Uses graphics to color and fill.
     */
    private void drawBackground(Graphics2D g) {
        //Fill background with water
        g.setPaint(OSMWayType.BACKGROUND.getColor());
        g.fill(viewRect);
    }

    /**
     * Draws OSMWayTypes.
     * @param g Uses graphics to color and fill.
     */
    private void drawOSMWayTypes(Graphics2D g) {
        for(OSMWayType osmWayType : OSMWayType.values()){
            if(osmWayType.equals(OSMWayType.BACKGROUND)) continue;
            if(osmWayType.getStroke() != null){
                if(osmWayType.equals(OSMWayType.PATH)) {
                    drawPath(g);
                } else {
                    drawShape(g,osmWayType);
                }
            } else if(osmWayType.getSizeDrawLevel() > 0){
                fillShapeWithSizeRange(g,osmWayType);
            } else {
                fillShape(g,osmWayType);
            }
        }
    }

    /**
     * Show point of interest.
     * @param g Uses graphics to drarMapPoint.
     */
    private void showPointsOfInterest(Graphics2D g) {
        if(showPois){
            for(PointOfInterest poi : PoiTab.getUiPois().getItems()){
                drawMapPoint(g,poi.getX(),poi.getY(),PathType.POI);
            }
        }
    }

    /**
     * Show map markings.
     * @param g Uses graphics to drawMapPoint.
     */
    private void showMapMarkings(Graphics2D g) {
        for (Map.Entry<PathType, OSMNode> entry : markingMap.entrySet()) {
            PathType key = entry.getKey();
            OSMNode value = entry.getValue();
            drawMapPoint(g,value.getLon(),value.getLat(),key);
        }
    }

    /**
     * Calculates the current level of details.
     */
    private void calculateCurrentLod() {
        for(int i = 0; i < LOD.length; i++) {
            if(i == LOD.length-1){
                if(LOD[i] > widthRatio){
                    currentLod = i;
                    break;
                }
            } else {
                if(LOD[i] < widthRatio) {
                    currentLod = i;
                    break;
                }
            }
        }
    }

    /**
     * Draws frames per second.
     * @param g Uses graphics to drawValue.
     * @param t1 Uses t1 to calculate the frames per second.
     */
    private void drawFPS(Graphics2D g, long t1) {
        if (showFPS) {
            resetTransform(g);
            long t2 = System.nanoTime();
            fps = (fps + 1e9 / (t2 - t1)) / 2;
            drawValue(g, "Fps", String.valueOf(Math.round(fps)), 5, 5);
        }
    }

    /**
     * Draws level of detail.
     * @param g Uses graphics to drawValue.
     */
    private void drawLOD(Graphics2D g) {
        if(showLOD){
            resetTransform(g);
            int y = 35;
            if(widthRatio > LOD[2]) y = 65;
            drawValue(g, "LOD", String.valueOf(currentLod), 5, y);
        }
    }

    /**
     * Draws coordinates.
     * @param g Uses graphics to resetTransform and drawValue.
     */
    private void drawCoordinates(Graphics2D g) {
        if(showCoordinates) {
            Point2D coordinate = null;
            boolean all = false;
            switch (coordinateType){
                case 0: coordinate = null; break;
                case 1: coordinate = MouseController.getRealPos();   break;
                case 2: coordinate = MouseController.getScreenPos(); break;
                case 3: coordinate = MouseController.getModelPos();  break;
                case 4: all = true; break;
            }
            if(coordinate != null || all){
                StringBuilder xVal= new StringBuilder();
                StringBuilder yVal= new StringBuilder();
                if(all){
                    xVal.append("Real: ").append(MouseController.getRealPos().getX());
                    xVal.append(" Screen: ").append((int)MouseController.getScreenPos().getX());
                    xVal.append(" Model: ").append(MouseController.getModelPos().getX());
                    yVal.append("Real: ").append(MouseController.getRealPos().getY());
                    yVal.append(" Screen: ").append((int)MouseController.getScreenPos().getY());
                    yVal.append(" Model: ").append(MouseController.getModelPos().getY());
                } else {
                    xVal.append(coordinate.getX());
                    yVal.append(coordinate.getY());
                }
                resetTransform(g);
                drawValue(g, "X", xVal.toString(), 5, getHeight() - 100);
                resetTransform(g);
                drawValue(g, "Y", yVal.toString(), 5, getHeight() - 70);
            }
        }
    }

    /**
     * Transform render frame.
     * @param g Uses graphics to transform.
     */
    private void transformRenderFrame(Graphics2D g) {
        g.transform(transform);
        try {
            viewRect = transform.createInverse().createTransformedShape(viewRect).getBounds2D();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create render frame.
     * @return The viewRect.
     */
    private Rectangle2D createRenderFrame() {
        if(WindowView.isDebugging()){
            return viewRect = new Rectangle2D.Double(
                    (getWidth()*debugViewportSizePct),
                    (getHeight()*debugViewportSizePct),
                    getWidth()-(getWidth()*debugViewportSizePct)-(getWidth()*debugViewportSizePct),
                    getHeight()-(getHeight()*debugViewportSizePct)-(getHeight()*debugViewportSizePct)-30
            );
        } else {
            return viewRect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Draws map bounds.
     * @param g Uses graphics to set stroke, paint and draw.
     */
    private void drawMapBounds(Graphics2D g) {
        g.setStroke(new BasicStroke(0.000f));
        g.setPaint(Color.BLACK);
        if (viewRect != null) {
            g.draw(viewRect);
            g.draw(new Line2D.Double(model.getMinLon(), model.getMinLat(), model.getMaxLon(), model.getMinLat()));
            g.draw(new Line2D.Double(model.getMinLon(),model.getMaxLat(),model.getMaxLon(),model.getMaxLat()));
            g.draw(new Line2D.Double(model.getMinLon(),model.getMinLat(),model.getMinLon(),model.getMaxLat()));
            g.draw(new Line2D.Double(model.getMaxLon(),model.getMinLat(),model.getMaxLon(),model.getMaxLat()));

        }
    }

    /**
     * Draws map point.
     * @param g Uses graphics to translate, setRenderingHint, fill, setColor.
     * @param x Uses x to get screen coordinates.
     * @param y Uses y to get screen coordinates.
     * @param type Uses type as a switch to which color should be used.
     */
    private void drawMapPoint(Graphics2D g, double x, double y, PathType type){
        resetTransform(g);
        Point2D p = toScreenCoords(new Point2D.Double(x,y));
        g.translate(p.getX(),p.getY()-20);
        switch (type){
            case START: g.setColor(Color.GREEN); break;
            case BREAKPOINT: g.setColor(Color.YELLOW); break;
            case END: g.setColor(Color.RED); break;
            case FINDPOINT: g.setColor(new Color(255, 89,0)); break;
            case POI: g.setColor(new Color(0,150,201)); break;
            default: g.setColor(Color.GRAY);break;
        }
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.fill(new Ellipse2D.Double(-10,-10,20,20));
        Polygon pg = new Polygon();
        pg.addPoint(-10,0);
        pg.addPoint(0,20);
        pg.addPoint(10,0);
        g.fillPolygon(pg);
        g.setColor(Color.BLACK);
        g.fill(new Ellipse2D.Double(-3,-3,7,7));
    }

    /**
     * Draws scale bar.
     * @param g Uses graphics to drawLine, drawFromLeft, paint, transform and rendering hints.
     */
    private void drawScaleBar(Graphics2D g){
        if(showScalebar){
            double lonTokm = viewRect.getWidth()*100;

            double km = (Math.round(lonTokm * 1000d) / 1000d)/8;

            String[] unit = {"m","km"};
            resetTransform(g);
            g.setPaint(Color.BLACK);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            if(km < 1){
                drawFromLeft(g,
                        (int)(km*1000) + " " + unit[0],
                        (getWidth()-15), getHeight()-55
                );
            } else {
                drawFromLeft(g,
                        new DecimalFormat("##.##",
                                WindowView.getLocalDecimalFormat()).format(km) + " " + unit[1],(getWidth()-15), getHeight()-55
                );
            }
            g.drawLine(((getWidth()-15)-(getWidth()/8)),getHeight()-50,getWidth()-15, getHeight()-50);
        }
    }

    /**
     * Draw from left.
     * @param g Uses graphics to drawString.
     * @param s Uses s to give the string which should be drawn.
     * @param x Uses x as to where the string should be.
     * @param y Uses y as to where the string should be.
     */
    private void drawFromLeft(Graphics2D g, String s, double x, double y){
        g.drawString(s,(int)x - g.getFontMetrics().stringWidth(s),(int)y);
    }

    /**
     * Set show point of interest.
     * @param showPois Uses the boolean to set or unset showPois.
     */
    public static void setShowPois(boolean showPois) {
        SwingView.showPois = showPois;
    }

    /**
     * Is show point of interest.
     * @return The boolean showPois.
     */
    public static boolean isShowPois() {
        return showPois;
    }

    /**
     * Reset transform.
     * @param g Uses graphics to transform.
     */
    private void resetTransform(Graphics2D g){
        g.setTransform(new AffineTransform());
    }

    /**
     * Draw value.
     * @param g Uses graphics to translate, paint, fill, setStroke, drawString and drawRect.
     * @param name Uses the string to describe the input.
     * @param value Uses the value to
     * @param x Uses x to translate.
     * @param y Uses y to translate.
     */
    private void drawValue(Graphics2D g, String name, String value, int x, int y) {
        String input = name + ": " + value;
        int padding = 5;
        int inputWidth = g.getFontMetrics().stringWidth(input)+padding;
        g.translate(x,y);
        g.setPaint(Color.WHITE);
        g.fillRect(0,0,inputWidth+padding,25);
        g.setPaint(Color.BLACK);
        g.setStroke(new BasicStroke(1f));
        g.drawRect(0,0,inputWidth+padding,25);
        g.drawString(input,padding,g.getFontMetrics().getHeight());
    }

    /**
     * Set KD type.
     * @param osm Uses osm as the kdtreeDebugType.
     */
    public static void setKdType(OSMWayType osm){
        kdtreeDebugType = osm;
    }

    /**
     * Draw KD tree.
     * @param g Uses graphics to set the color and draw.
     */
    private void drawKDTree(Graphics2D g) {
        if(showKD){
            if(model.getKdTreeEnumMap().get(kdtreeDebugType) != null){
                Node n = model.getKdTreeEnumMap().get(kdtreeDebugType).getRoot();
                g.setPaint(Color.RED);
                Line2D root = new Line2D.Double(n.getValue(),viewRect.getMinY(), n.getValue(),viewRect.getMaxY());
                g.draw(root);
                drawYNode(g, n, viewRect.getMinY(),  viewRect.getMinX(), true);
            }
        }
    }

    /**
     * Draw x node.
     * @param g Uses graphics to paint and draw the x node.
     * @param n Uses node to make the line, get the value and get the right child.
     * @param x Uses x from drawYNode.
     * @param y Uses y to make the line, get the left child and get the right child.
     * @param goingRight Uses boolean to check if going right.
     */
    private void drawXNode(Graphics2D g, Node n, double x, double y, boolean goingRight) {
        if(n.getValue()==0)return;
        g.setPaint(Color.BLUE);
        Shape line;
        if(goingRight){
            line = new Line2D.Double(y, n.getValue(), n.getRightChild().getValue(), n.getValue());
        } else {
            line = new Line2D.Double(n.getLeftChild().getValue(), n.getValue(), y, n.getValue());
        }
        g.draw(line);
        if(n.getLeftChild()!=null)drawYNode(g, n.getLeftChild(), n.getValue(), y, false);
        if(n.getRightChild()!=null)drawYNode(g, n.getRightChild(), n.getValue(), y,true);

    }

    /**
     * Draw y node.
     * @param g Uses graphics to paint and draw the x node.
     * @param n Uses node to make the line, get the value and get the right child.
     * @param x Uses x to make the line, get the left child and get the right child.
     * @param y Uses y from drawXNode.
     * @param goingUp Uses boolean to check if going up.
     */
    private void drawYNode(Graphics2D g, Node n, double x, double y, boolean goingUp) {
        if(n.getValue()==0)return;
        g.setPaint(Color.RED);
        Shape line;
        if(goingUp){
            line = new Line2D.Double(n.getValue(),x, n.getValue(),n.getRightChild().getValue());
        } else {
            line = new Line2D.Double(n.getValue(), n.getLeftChild().getValue() , n.getValue(), x);
        }
        g.draw(line);
        if(n.getLeftChild()!=null)drawXNode(g, n.getLeftChild(), x, n.getValue(), false);
        if(n.getRightChild()!=null)drawXNode(g, n.getRightChild(), x, n.getValue(),true);
    }

    /**
     * Show debug nearest road.
     * @param g Uses graphics to drawValue.
     * @param nearestName Uses nearestName to name the value.
     */
    private void showDebugNearestRoad(Graphics2D g, String nearestName) {
        resetTransform(g);
        drawValue(g, "Closest: ", nearestName,
                5,35
        );
    }

    /**
     * Show nearest road name.
     * @param g Uses graphics to showDebugNearestRoad.
     * @param debug Uses the boolean debug to decide showDebugNearestRoad.
     */
    private void showNearestRoadName(Graphics2D g, boolean debug) {
        if(MouseController.getLastMousePosition() != null){
            if(widthRatio > LOD[2]){
                Point2D mouse = toModelCoords(MouseController.getLastMousePosition());
                String nearestName = Model.getInstance().getNearestName(OSMWayType.ROAD, mouse);
                if(debug){
                    showDebugNearestRoad(g, nearestName);
                }
                MapContextMenu.setNearestName(nearestName);

                if(!tooltip.getText().equals(nearestName))
                {
                    tooltip.setText(nearestName);
                }
                if(showToolTip) setToolTip(true);
            } else {
                MapContextMenu.setNearestName(null);
                setToolTip(false);
            }
        }
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>.
     */
    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    /**
     * Toggle antialiasing.
     */
    public void toggleAntiAliasing() {
        useAntiAliasing = !useAntiAliasing;
        repaint();
    }

    /**
     * Set use antialiasing.
     * @param b Uses the boolean to set useAntiAliasing.
     */
    public void setUseAntiAliasing(boolean b){
        useAntiAliasing = b;
        repaint();
    }

    /**
     * Set absolute position.
     * @param x Uses x to zoom.
     * @param y Uses y to zoom.
     */
    public void setAbsPosition(double x, double y){
        transform.setToIdentity();
        zoom(200000,-x,-y);
        zoomPan(getWidth()/2,getHeight()/2);
    }

    public void setAbsPosition(){
        transform.setToIdentity();

    }


    /**
     * Pan.
     * @param dx Uses dx to getTranslateInstance.
     * @param dy Uses dy to getTranslateInstance.
     */
    public void pan(double dx, double dy) {
        //dx+ = To the left <-
        //dx- = To the right ->
        //dy+ = To the top /\
        //dy- = To the bot \/
        if(viewRect != null){
            if((model.getMinLon() >= viewRect.getMinX() && dx > 0) || (model.getMaxLon() <= viewRect.getMaxX() && dx < 0)) {
                dx = 0;
            }
            if((model.getMinLat() <= viewRect.getMaxY() && dy < 0) || (model.getMaxLat() >= viewRect.getMinY() && dy > 0)) {
                dy = 0;
            }
            transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
            repaint();
        } else {
            transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
            repaint();
        }
    }

    /**
     * Zoom pan.
     * @param dx Uses dx to getTranslateInstance.
     * @param dy Uses dy to getTranslateInstance.
     */
    public void zoomPan(double dx, double dy){
        transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
        repaint();
    }

    /**
     * Get the width ratio.
     * @return The widthRatio.
     */
    private double getWidthRatio(){
        return widthRatio;
    }

    /**
     * Zoom to center.
     * @param factor Uses the factor to zoom with.
     */
    public void zoomToCenter(double factor) {
        zoom(factor, -getWidth() /2, -getHeight() / 2);
    }

    /**
     * Zoom.
     * @param factor Uses the factor to scale the instance.
     * @param x Uses x to zoom pan.
     * @param y Uses y to zoom pan.
     */
    public void zoom(double factor, double x, double y) {
        if(viewRect != null) {
            if(viewRect.getWidth() > (model.getMaxLon() - model.getMinLon())*1.5 && factor < 1) factor = 1;
            zoomPan(x, y);
            transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
            zoomPan(-x, -y);
        } else {
            zoomPan(x, y);
            transform.preConcatenate(AffineTransform.getScaleInstance(factor, factor));
            zoomPan(-x, -y);
        }
        repaint();
    }

    /**
     * Smooth zoom.
     * @param value Uses value to get the factor.
     * @param x Uses x to zoom.
     * @param y Uses y to zoom.
     */
    public void smoothZoom(double value, double x, double y){
        double factor = pow(1.1, value*0.5);
        if(getWidthRatio() < pow(10,6)){
            zoom(factor,x,y);
        } else if(getWidthRatio() > pow(10,6) && value < 0) {
            zoom(factor, x, y);
        }
    }

    /**
     * Smooth zoom to center.
     * @param value Uses value to get the factor.
     */
    public void smoothZoomToCenter(Double value){
        Double factor = pow(1.1, value*0.5);
        if(getWidthRatio() < pow(10,6)){
            zoomToCenter(factor);
        } else if(getWidthRatio() > pow(10,6) && value < 0){
            zoomToCenter(factor);
        }
    }

    /**
     * To model coordinates.
     * @param p Uses point to inverseTransform.
     * @return Method: transform.inverseTransform(p, null) or null.
     */
    public Point2D toModelCoords(Point2D p) {
        try {
            return transform.inverseTransform(p, null);
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * To screen coordinates.
     * @param p Uses point to inverseTransform.
     * @return Method: transform.transform(p,null).
     */
    public Point2D toScreenCoords(Point2D p){
        return transform.transform(p,null);
    }

    /**
     * Checks if antialiasing is in use.
     * @return The boolean useAntiAliasing.
     */
    public boolean isUseAntiAliasing() {
        return useAntiAliasing;
    }

    /**
     * Checks if show frame per second is in use.
     * @return The boolean showFPS.
     */
    public boolean isShowFPS() {
        return showFPS;
    }

    /**
     * Set show frame per second.
     * @param showFPS Uses the boolean showFPS to set showFPS.
     */
    public void setShowFPS(boolean showFPS) {
        SwingView.showFPS = showFPS;
    }

    /**
     * Add point of interest.
     * @param x Uses x to set new PointOfInterest.
     * @param y Uses y to set new PointOfInterest.
     */
    public void addPointOfInterest(double x, double y) {
        model.addPOI(new PointOfInterest(x,y));
    }

    /**
     * Get the view rect.
     * @return The viewRect.
     */
    public Rectangle2D getViewRect() {
        return viewRect;
    }

    /**
     * Get marking map.
     * @return The markingMap.
     */
    public static HashMap<PathType, OSMNode> getMarkingMap() {
        return markingMap;
    }

    /**
     * Get level of detail.
     * @return The level of detail.
     */
    public static int[] getLOD() {
        return LOD;
    }

    /**
     * Set show KD.
     * @param b Uses the boolean to set showKD.
     */
    public static void setShowKD(boolean b) {
        showKD = b;
    }

    /**
     * Set debug viewport size in percent.
     * @param pct Uses percent to fill fill the border around the debugView.
     */
    public static void setDebugViewportSizePct(double pct){
        debugViewportSizePct = pct;
    }

    /**
     * Set show level of detail.
     * @param showLOD Uses the boolean show level of detail to set show level of detail.
     */
    public static void setShowLOD(boolean showLOD) {
        SwingView.showLOD = showLOD;
    }

    /**
     * Set show coordinates.
     * @param showCoordinates Uses the boolean showCoordinates to set show coordinates.
     */
    public static void setShowCoordinates(boolean showCoordinates) {
        SwingView.showCoordinates = showCoordinates;
    }

    /**
     * Set show scale bar.
     * @param showScalebar Uses the boolean showScaleBar to set show scale bar.
     */
    public static void setShowScalebar(boolean showScalebar) {
        SwingView.showScalebar = showScalebar;
    }

    /**
     * Set the show tooltip.
     * @param showToolTip Uses the boolean showToolTip, to set show tooltip.
     */
    public static void setShowToolTip(boolean showToolTip) {
        SwingView.showToolTip = showToolTip;
    }

    /**
     * Set tooltip.
     * @param activated Checks if activated.
     */
    private static void setToolTip(boolean activated) {
        if(activated){
            Tooltip.install(MapView.getSwingNode(),tooltip);
        } else {
            Tooltip.uninstall(MapView.getSwingNode(),tooltip);
        }
    }

    /**
     * Set the coordinate type.
     * @param coordinateType Uses coordinateType to set coordinateType.
     */
    public static void setCoordinateType(int coordinateType) {
        SwingView.coordinateType = coordinateType;
    }

    /**
     * Get coordinate type.
     *
     * @return Gives the coordinateType.
     */
    public int getCoordinateType() {
        return coordinateType;
    }
}
