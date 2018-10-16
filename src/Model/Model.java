package Model;

import Controller.UIAction.WindowAction.Option;
import KdTree.KdTree;
import KdTree.KdTreeThreadKeeper;


import Model.Graph.DirectedEdge;
import Model.Graph.EdgeWeightedDigraph;

import Model.Helpers.LongToOSMNodeMap;
import Model.OSM.*;
import View.Content.Components.AutoComplete;
import View.Content.MapContext.MapView;
import View.Content.MapContext.SwingView;
import View.Content.Tabs.PoiTab;
import View.ThemeManager;
import View.WindowView;

import javafx.application.Preloader;
import javafx.scene.control.ListView;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

import static View.WindowView.debugPrintln;

/**
 * This class handles the processing of osm-data
 * and converts it into displayable shapes.
 */
public class Model extends Observable {
    private EnumMap<OSMWayType, List<Shape>> shapes = initializeMap();
    private Map<OSMWayType, KdTree> kdTreeEnumMap;
    private static double minLat, minLon, maxLat, maxLon, lonFactor;
    private static Model instance;
    private Shape path;
    private ArrayList<OSMAddress> OSMAddresses;
    private Set<Shape> selectedAddresses;

    private static List<PointOfInterest> mapPois = new ArrayList<>();
    private static ListView<Option> options = new ListView<>();
    private static int nonDebugOptions;

    /**
     * Constructor for Model.
     */
    public Model() {
        selectedAddresses = new HashSet<>();
    }

    /**
     * Gets the instance of Model.
     * @return Model instance.
     */
    public static Model getInstance(){
        if(instance  == null) instance = new Model();
        return instance;
    }

    public void load(String filename){
        if(WindowView.isJAR())JARLoad("/data/"+filename);
        else {
            File ser = new File("data/"+filename);
            if (ser.exists() && !ser.isDirectory()) {
                fileLoad("data/"+filename);
                if(WindowView.isDebugging())save("data/map.gz");
            }
        }
    }

    /**
     * Gets the integer of the options in preferenceAction that is not debug-related options.
     * @return The integer.
     */
    public static int getNonDebugOptions() {
        return nonDebugOptions;
    }

    /**
     * Sets the integer of the options in preferenceAction that is not debug-related options.
     * @param nonDebugOptions Uses nonDebugOptions to set nonDebugOptions.
     */
    public static void setNonDebugOptions(int nonDebugOptions) {
        Model.nonDebugOptions = nonDebugOptions;
    }

    /**
     * Removes the drawn path.
     */
    void emptyPaths() {
        path = null;
    }

    /**
     * This method works just like fileLoad-method, but it reads a jar.
     * @param filename The jar.
     */
    private void JARLoad(String filename) {
        Exception err = null;
        long totalMillis = System.currentTimeMillis();
        try {
            GZIPInputStream gis = new GZIPInputStream(this.getClass().getResourceAsStream(filename));
            ObjectInputStream is = new ObjectInputStream(gis);
            long millis = System.currentTimeMillis();
            kdTreeEnumMap = (Map<OSMWayType, KdTree>) is.readObject();
            debugPrintln("KDTrees done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
            millis = System.currentTimeMillis();
            OSMAddresses = (ArrayList<OSMAddress>) is.readObject();
            debugPrintln("Addresses done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
            millis = System.currentTimeMillis();
            EdgeWeightedDigraph.setAdj((List<DirectedEdge>[])is.readObject());
            debugPrintln("Edges done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
            millis = System.currentTimeMillis();
            minLon = (double) is.readObject();
            minLat = (double) is.readObject();
            maxLon = (double) is.readObject();
            maxLat = (double) is.readObject();
            lonFactor = (double) is.readObject();
            debugPrintln("Long/Lat done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace(); err = e;
        }
        if(WindowView.isDebugging() && err == null) System.out.println("-Loaded successfully in "+(System.currentTimeMillis()-totalMillis)/1000+" secs-");
    }

    /**
     * Loads the file.
     * @param filename Uses filename to fileLoad specific file.
     */
    public Model(String filename) {
        fileLoad(filename);
    }

    /**
     * Initializes the map by iterating through every NamedShape of different OSMWayTypes and adding it to the map.
     * @return The enumMap.
     */
    private EnumMap<OSMWayType, List<Shape>> initializeMap() {
        EnumMap<OSMWayType, List<Shape>> map = new EnumMap<>(OSMWayType.class);
        for (OSMWayType type: OSMWayType.values()) {
            map.put(type, new ArrayList<>());
        }
        return map;
    }

    /**
     * Adds a shape to the EnumMap shapes.
     * @param type The defined OSMWayType.
     * @param shape The Shape.
     */
    public void add(OSMWayType type, Shape shape) {
        shapes.get(type).add(shape);
        dirty();
    }

    /**
     * Gets the addresses the user writes in the textfield.
     * @return Set of the addresses.
     */
    public Set<Shape> getSelectedAddresses() {
        return selectedAddresses;
    }

    /**
     * Informs the observers that the data has changed.
     */
    private void dirty() {
        setChanged();
        notifyObservers();
    }

    /**
     * Reads the OSM file.
     * @param filename The osmFile.
     */
    private void readFromOSM(InputSource filename) {
        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            OSMHandler osmHandler = new OSMHandler();
            xmlReader.setContentHandler(osmHandler);
            xmlReader.parse(filename);
            lonFactor = 1/osmHandler.getLonFactor();
            OSMAddresses= osmHandler.getOSMAddresses();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves to gz.
     * @param filename The file to be saved.
     */
    public void save(String filename) {
        debugPrintln("Saving " + filename);
        Exception err = null;
        long totalMillis = System.currentTimeMillis();
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream os;
            GZIPOutputStream gz = null;
            if(filename.endsWith(".gz")){
                gz = new GZIPOutputStream(fos);
                os = new ObjectOutputStream(gz);
            } else {
                os = new ObjectOutputStream(fos);
            }
            long millis = System.currentTimeMillis();
            os.writeObject(kdTreeEnumMap);
            debugPrintln("KDTrees done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
            millis = System.currentTimeMillis();
            os.writeObject(OSMAddresses);
            debugPrintln("Addresses done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
            millis = System.currentTimeMillis();
            os.writeObject(EdgeWeightedDigraph.getInstance().getAdj());
            debugPrintln("Edges done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
            millis = System.currentTimeMillis();
            os.writeObject(minLon);
            os.writeObject(minLat);
            os.writeObject(maxLon);
            os.writeObject(maxLat);
            os.writeObject(lonFactor);
            debugPrintln("Long/Lat done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
            os.close();
            if(gz != null) gz.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace(); err = e;
        }
        if(WindowView.isDebugging() && err == null) System.out.println("-Saved successfully in " + (System.currentTimeMillis()-totalMillis)/1000 + " secs-");
    }

    /**
     * Get longitude factor.
     * @return The reciprocal value for width-scaling.
     */
    public static double getLonFactor() {
        return lonFactor;
    }

    /**
     * Loads the file in the given directory.
     * @param fileDir File directory.
     */
    public void fileLoad(String fileDir) {
        String fileName = new File(fileDir).getName();
        kdTreeEnumMap=null;
        Exception err = null;
        long totalMillis = System.currentTimeMillis();
        if (fileDir.endsWith(".osm")) {
            debugPrintln("Parsing " + fileName);
            readFromOSM(new InputSource(fileDir));
            listToKDTree();
        } else if (fileDir.endsWith(".zip")) {
            debugPrintln("Parsing " + fileName);
            try {
                ZipInputStream zis = new ZipInputStream(new FileInputStream(fileDir));
                zis.getNextEntry();
                readFromOSM(new InputSource(zis));
                listToKDTree();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace(); err = e;
            }
        } else if (fileDir.endsWith(".bin") || fileDir.endsWith(".ser")) {
            debugPrintln("Loading " + fileDir);
            try {
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileDir));
                kdTreeEnumMap = (Map<OSMWayType, KdTree>) is.readObject();
                OSMAddresses = (ArrayList<OSMAddress>) is.readObject();
                EdgeWeightedDigraph.setAdj((List<DirectedEdge>[])is.readObject());
                minLon = (double) is.readObject();
                minLat = (double) is.readObject();
                maxLon = (double) is.readObject();
                maxLat = (double) is.readObject();
                lonFactor = (double) is.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if(fileDir.endsWith(".gz")){
            debugPrintln("Loading " + fileDir);
            GZIPInputStream gis;
            try {
                gis = new GZIPInputStream(new FileInputStream(fileDir));
                ObjectInputStream is = new ObjectInputStream(gis);
                long millis = System.currentTimeMillis();
                kdTreeEnumMap = (Map<OSMWayType, KdTree>) is.readObject();
                debugPrintln("KDTree done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
                millis = System.currentTimeMillis();
                OSMAddresses = (ArrayList<OSMAddress>) is.readObject();
                debugPrintln("Addresses done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
                millis = System.currentTimeMillis();
                EdgeWeightedDigraph.setAdj((List<DirectedEdge>[])is.readObject());
                debugPrintln("Edges done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
                millis = System.currentTimeMillis();
                minLon = (double) is.readObject();
                minLat = (double) is.readObject();
                maxLon = (double) is.readObject();
                maxLat = (double) is.readObject();
                lonFactor = (double) is.readObject();
                debugPrintln("Long/Lat done in: " + ((System.currentTimeMillis()-millis)/1000 + "s"));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace(); err = e;
                if(e.getClass().equals(InvalidClassException.class)) debugPrintln("This file is incompatible with the current version of the program");
            }
        }
        dirty();
        if(WindowView.isDebugging() && err == null) System.out.println("-Loaded successfully in "+(System.currentTimeMillis()-totalMillis)/1000+" secs-");
        AutoComplete.setAddressList(Model.getInstance().getOSMAddresses());
    }

    /**
     * Converts a list to a KdTree.
     */
    private void listToKDTree(){
        shapes.remove(OSMWayType.UNKNOWN);
        KdTreeThreadKeeper kdTreeKeeper = new KdTreeThreadKeeper(shapes);
        kdTreeEnumMap = kdTreeKeeper.getKdMap();
        shapes = initializeMap();
        KdTreeThreadKeeper.nullStaticResult();
    }

    /**
     * Returns the set of namedShapes.
     * @param type OSMWayType.
     * @param left The left boundary of the view.
     * @param bottom The bottom boundary of the view.
     * @param right The right boundary of the view.
     * @param top The top boundary of the view.
     * @return The set of namedShapes.
     */
    public Set<Shape> get(OSMWayType type,double left,double bottom,double right,double top) {
        KdTree kdTree = kdTreeEnumMap.get(type);
        if(kdTree!=null)return kdTree.rangeSearch(left,bottom,right,top);
        return new HashSet<>();
    }

    /**
     * Returns the minimum latitude.
     * @return The minLat.
     */
    public double getMinLat() {
        return minLat;
    }

    /**
     * Returns the minimum longitude.
     * @return The minLon.
     */
    public double getMinLon() {
        return minLon;
    }

    /**
     * Returns the maximum latitude.
     * @return The maxLat.
     */
    public double getMaxLat() {
        return maxLat;
    }

    /**
     * Returns the maximum longitude.
     * @return The maxLon.
     */
    public double getMaxLon() {
        return maxLon;
    }

    /**
     * Returns the KdTreeEnumMap.
     * @return Map.
     */
    public Map<OSMWayType,KdTree> getKdTreeEnumMap() {
        return kdTreeEnumMap;
    }

    /**
     * Sets the map points of interest.
     * @param mapPois List of map points of interest.
     */
    public static void setMapPois(Object mapPois) {
        Model.mapPois = (List<PointOfInterest>) mapPois;
    }

    /**
     * Returns the options of the listView.
     * @return Options.
     */
    public static ListView getOptions() {
        return options;
    }

    /**
     * Finds the nearest road node neighbour of the mouse point and returns the roadname.
     * @param osmWayType Type.
     * @param point2D Mouse point.
     * @return Nearest name.
     */
    public String getNearestName(OSMWayType osmWayType, Point2D point2D){
        Rectangle2D viewRect = MapView.getSwingView().getViewRect();
        return Model.getInstance().getKdTreeEnumMap().get(osmWayType).NearestNeighbour(
                point2D,
                viewRect.getX(),
                viewRect.getMaxY(),
                viewRect.getX() + viewRect.getWidth(),
                viewRect.getY()
        ).getName();
    }

    /**
     * Adds a point of interest.
     * @param pointOfInterest Point of interest.
     */
    public void addPOI(PointOfInterest pointOfInterest) {
        PoiTab.getUiPois().getItems().add(pointOfInterest);
        pointOfInterest.maxWidthProperty().bind(PoiTab.getUiPois().widthProperty());
    }

    /**
     * Removes a point of interest.
     * @param pointOfInterest Point of interest.
     */
    void removePOI(PointOfInterest pointOfInterest) {
        PoiTab.getUiPois().getItems().remove(pointOfInterest);
    }

    /**
     * Adds the path.
     * @param p Path.
     */
    public void addPath(Shape p) {
        path = p;
    }

    /**
     * Returns the path.
     * @return Path.
     */
    public Shape getPath() {
        return path;
    }

    /**
     * Returns the nearest address by iterating through the list of OSMAddresses.
     * @param lon X coordinate.
     * @param lat Y coordinate.
     * @return OSMAddress.
     */
    public OSMAddress getNearestAddress(double lon, double lat) {
        OSMAddress closestAddress = null;
        double dist = Double.POSITIVE_INFINITY;
        for (OSMAddress addr : OSMAddresses) {
            double tempDist = Math.sqrt(Math.pow(Math.abs(lat-addr.getLat()),2.0)+Math.pow(Math.abs(lon-addr.getLon()),2.0));
            if(tempDist<dist) {
                dist = tempDist;
                closestAddress = addr;
            }
        }
        return closestAddress;
    }

    /**
     * Returns a list of points of interest.
     * @return List of points of interest.
     */
    public List<PointOfInterest> getMapPois() {
        return mapPois;
    }

    /**
     * Saves a generic object to the chosen file.
     * @param object Object.
     * @param fileName File.
     */
    public void saveObject(Object object, String fileName){
        if(object != null){
            debugPrintln("Saving: " + object.getClass() + " called: " + fileName);
            try {
                ThemeManager.createDir(WindowView.getDataFolderName());
                FileOutputStream fos;
                if(WindowView.isJAR()) fos = new FileOutputStream(WindowView.getDataFolderName() + "/" + fileName+".gz");
                else fos = new FileOutputStream(WindowView.getDataFolderName() + "\\" + fileName+".gz");
                GZIPOutputStream gz = new GZIPOutputStream(fos);
                ObjectOutputStream os = new ObjectOutputStream(gz);
                os.writeObject(object);
                os.close();
                gz.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads a generic object from the specified file.
     * @param className Object to fileLoad.
     * @param fileName File.
     * @return Object.
     */
    public Object loadObject(Class className, String fileName){
        GZIPInputStream gis;
        try {
            debugPrintln("Loading: " + className + " called: " + fileName);
            if(WindowView.isJAR()) gis = new GZIPInputStream(new FileInputStream(WindowView.getDataFolderName()+"/"+fileName+".gz"));
            else gis = new GZIPInputStream(new FileInputStream(WindowView.getDataFolderName()+"\\"+fileName+".gz"));
            ObjectInputStream is = new ObjectInputStream(gis);
            return className.cast(is.readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Class for handling OSMFiles,
     */
    private class OSMHandler extends DefaultHandler {
        LongToOSMNodeMap idToNode = new LongToOSMNodeMap(25);
        Map<Long, OSMWay> idToWay = new HashMap<>();
        HashMap<OSMNode,OSMWay> coastlines = new HashMap<>();
        ArrayList<OSMAddress> OSMAddresses = new ArrayList<>();
        OSMWay way;
        private final short EARTH_RADIUS_KM = 6371;
        private double lonFactor;
        private OSMWayType type;
        private OSMRelation relation;
        private String  city, houseNumber;
        private String postcode;
        private float lon,lat;
        private String duration;
        private String speedLimit;
        private String roadName;
        private boolean oneWay, bikeAllowed, walkAllowed, carAllowed;
        private int arrayId = 0;

        /**
         * Returns the list of addresses.
         * @return List.
         */
        private ArrayList<OSMAddress> getOSMAddresses() {
            idToNode = null;
            return OSMAddresses;
        }


        /**
         * Returns the factor to convert the map-longitude to the real-world longitude.
         * @return Factor.
         */
        double getLonFactor(){
            return lonFactor;
        }

        /**
         * Converts degrees to radians.
         * @param degree Uses the degree to convert to radian.
         * @return radian.
         */
        private double degreeToRadian(double degree){
            return Math.abs(degree)*Math.PI/180;
        }

        /**
         * Start element for xml key, value elements.
         * @param uri From super class.
         * @param localName From super class.
         * @param qName The query name which decides which OSMWayType.
         * @param attributes Get the attributes from the query name.
         * @throws SAXException Error from the XML parser.
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (qName) {
                case "bounds":
                    minLat = Double.parseDouble(attributes.getValue("minlat"));
                    minLon = Double.parseDouble(attributes.getValue("minlon"));
                    maxLat = Double.parseDouble(attributes.getValue("maxlat"));
                    maxLon = Double.parseDouble(attributes.getValue("maxlon"));
                    double avgLat = minLat + (maxLat - minLat) / 2;
                    lonFactor = Math.cos(avgLat / 180 * Math.PI);
                    minLon *= lonFactor;
                    maxLon *= lonFactor;
                    maxLat = -maxLat;
                    minLat = -minLat;
                    break;
                case "node":
                    double lonTemp = Double.parseDouble(attributes.getValue("lon"));
                    double latTemp = Double.parseDouble(attributes.getValue("lat"));
                    lon = (float)(lonFactor * lonTemp);
                    lat = (float)-latTemp;
                    long id = Long.parseLong(attributes.getValue("id"));
                    idToNode.put(id, lon, lat);
                    idToNode.setArrayid(id,arrayId);
                    arrayId++;
                    break;
                case "way":
                    way = new OSMWay();
                    type = OSMWayType.UNKNOWN;
                    idToWay.put(Long.parseLong(attributes.getValue("id")), way);
                    break;
                case "relation":
                    relation = new OSMRelation();
                    type = OSMWayType.UNKNOWN;
                    break;
                case "member":
                    OSMWay w = idToWay.get(Long.parseLong(attributes.getValue("ref")));
                    if (w != null) relation.add(w);
                    break;
                case "nd":
                    long ref = Long.parseLong(attributes.getValue("ref"));
                    OSMNode node = idToNode.get(ref);
                    way.add(node);
                    break;
                case "tag":
                    switch (attributes.getValue("k")) {
                        case "maxspeed":
                            if(way!=null) speedLimit =(attributes.getValue("v"));
                            break;
                        case "bicycle" :
                            type = OSMWayType.BIKEPATH;
                            if(way!=null) {
                                if (!attributes.getValue("v").equals("no")) bikeAllowed=true;
                            }
                            break;
                        case "foot" :
                            type = OSMWayType.FOOTWAY;
                            if(way!=null) {
                                if (!attributes.getValue("v").equals("no")) walkAllowed=true;
                            }
                            break;
                        case "oneway" :
                            if(way!=null) {
                                if (attributes.getValue("v").equals("yes")) oneWay=true;
                            }
                            break;
                        case "name":
                            if (way!=null) {
                                roadName = (attributes.getValue("v"));
                            }
                            break;
                        case "highway":
                            type = OSMWayType.ROAD;
                            if(way!=null) {
                                switch (attributes.getValue("v")) {
                                    case"footway":
                                    case"service":
                                    type = OSMWayType.FOOTWAY;
                                    walkAllowed =true;
                                    bikeAllowed =true;
                                break;
                                    case"primary":
                                    type = OSMWayType.HIGHWAY;
                                    carAllowed =true;
                                break;
                                    case"trunk":
                                    type = OSMWayType.HIGHWAY;
                                    carAllowed =true;
                                break;
                                    case"cycleway":
                                    type = OSMWayType.BIKEPATH;
                                    bikeAllowed =true;
                                    carAllowed =false;
                                break;
                                    default:
                                    walkAllowed =true;
                                    bikeAllowed =true;
                                    carAllowed =true;
                                    break;
                                }
                            }
                            break;
                        case "railway":
                            type = OSMWayType.RAILWAY;
                            if (attributes.getValue("v").equals("subway")) {
                                type = OSMWayType.SUBWAY;
                            }
                            if(way!=null) {
                                walkAllowed = false;
                                bikeAllowed = false;
                                carAllowed = false;
                            }
                            break;
                        case "landuse":
                            if(attributes.getValue("v").equals("grass")) {
                                type = OSMWayType.LANDUSE;
                            }
                            break;
                        case "leisure":
                            if(attributes.getValue("v").equals("park") || attributes.getValue("v").equals("garden")){
                                type = OSMWayType.PARK;
                            }
                            break;
                        case "natural":
                            if (attributes.getValue("v").equals("water")) {
                                type = OSMWayType.WATER;
                            } else if(attributes.getValue("v").equals("coastline")) {
                                type = OSMWayType.COASTLINE;
                            }
                            break;
                        case "building":
                            type = OSMWayType.BUILDING;
                            break;
                        case "amenity":
                            if (attributes.getValue("v").equals("university") ||
                                    attributes.getValue("v").equals("school") ||
                                    attributes.getValue("v").equals("kindergarten") ||
                                    attributes.getValue("v").equals("college") ||
                                    attributes.getValue("v").equals("library") ||
                                    attributes.getValue("v").equals("archive") ||
                                    attributes.getValue("v").equals("public_bookcase") ||
                                    attributes.getValue("v").equals("music_school") ||
                                    attributes.getValue("v").equals("driving_school") ||
                                    attributes.getValue("v").equals("language_school") ||
                                    attributes.getValue("v").equals("research_institute")) {
                                type = OSMWayType.EDUCATION;
                            }

                            if (attributes.getValue("v").equals("clinic") ||
                                    attributes.getValue("v").equals("dentist") ||
                                    attributes.getValue("v").equals("doctors") ||
                                    attributes.getValue("v").equals("hospital") ||
                                    attributes.getValue("v").equals("nursing_home") ||
                                    attributes.getValue("v").equals("pharmacy") ||
                                    attributes.getValue("v").equals("social_facility") ||
                                    attributes.getValue("v").equals("veterinary") ||
                                    attributes.getValue("v").equals("blood_donation")) {
                                type = OSMWayType.HEALTHCARE;
                            }

                            if(attributes.getValue("v").equals("atm") ||
                                    attributes.getValue("v").equals("bank")) {
                                type = OSMWayType.FINANCIAL;

                                if(attributes.getValue("v").equals("bicycle_rental") ||
                                        attributes.getValue("v").equals("bus_station") ||
                                        attributes.getValue("v").equals("parking") ||
                                        attributes.getValue("v").equals("ferry_terminal") ||
                                        attributes.getValue("v").equals("motorcycle_parking") ||
                                        attributes.getValue("v").equals("parking_space") ||
                                        attributes.getValue("v").equals("taxi") ||
                                        attributes.getValue("v").equals("ticket_validator") ||
                                        attributes.getValue("v").equals("boat_rental") ||
                                        attributes.getValue("v").equals("car_rental")) {
                                    type = OSMWayType.TRANSPORTATION;
                                }

                                if(attributes.getValue("v").equals("car_wash") ||
                                        attributes.getValue("v").equals("charging_station") ||
                                        attributes.getValue("v").equals("fuel") ||
                                        attributes.getValue("v").equals("bicycle_repair_station")) {
                                    type = OSMWayType.SERVICES;
                                }
                            }
                            break;
                        case "duration":
                            if (way != null) {
                                duration = attributes.getValue("v");
                            }
                            break;
                        case "route":
                            if (attributes.getValue("v").equals("ferry") && way != null) {
                                type = OSMWayType.FERRY;
                                carAllowed = true;
                                bikeAllowed = true;
                                walkAllowed = true;
                            }
                            break;
                        case "addr:city":
                            city = attributes.getValue("v");
                            break;
                        case "addr:housenumber":
                            houseNumber = attributes.getValue("v");
                            break;
                        case "addr:postcode":
                            postcode = attributes.getValue("v");
                            break;
                        case "addr:street":
                            String street = attributes.getValue("v");
                            if(city!=null && houseNumber!=null && postcode!=null){
                                OSMAddress addressObject = new OSMAddress(lon,lat,street,city,houseNumber,postcode);
                                OSMAddresses.add(addressObject);
                            }
                            city=null;
                            houseNumber=null;
                            postcode=null;

                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }

        /**
         * The last element to be loaded into the OSMWay.
         * @param uri From super class.
         * @param localName From super class.
         * @param qName Query name for the different values there are.
         * @throws SAXException Error from the XML parser.
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            Path2D path = new Path2D.Float();
            OSMNode node;
            switch (qName) {
                case "way":
                    if(type==OSMWayType.COASTLINE){
                        //stitch coastlines together and search for coastlines that can be merged with current way
                        OSMWay before = coastlines.remove(way.from());
                        OSMWay after = coastlines.remove(way.to());
                        OSMWay merged = new OSMWay();
                        //Add the three paths together
                        if(before!=null) {
                            merged.addAll(before.subList(0,before.size()-1));
                        }
                        merged.addAll(way);
                        if(after!=null && after != before) {
                            merged.addAll(after.subList(1,after.size()));
                        }
                        coastlines.put(merged.to(),merged);
                        coastlines.put(merged.from(),merged);
                    } else {
                        Short speed;

                        if(speedLimit != null) {
                            if (speedLimit.matches("[0-9]{0,3}")) {
                                speed = Short.valueOf(speedLimit);
                            } else if(speedLimit.equals("DK:rural")){
                                speed = 80;
                            } else if(speedLimit.equals("DK:motorway")){
                                speed = 130;
                            } else {
                                speed = 50;
                            }
                        } else {
                            speed = 50;
                        }

                        for(int i = 0; i < way.size()-1; i++){
                            OSMNode start = way.get(i);
                            OSMNode end = way.get(i+1);

                            double deltaLat = degreeToRadian(start.getLat()-end.getLat());
                            double deltaLon = degreeToRadian(start.getLon()-end.getLon());
                            double Lat1 = degreeToRadian(start.getLat());
                            double Lat2 = degreeToRadian(end.getLat());
                            double variableA = Math.pow(Math.sin(deltaLat/2),2) + Math.pow(Math.sin(deltaLon/2),2)*Math.cos(Lat1)*Math.cos(Lat2);
                            double variableC = 2 * Math.atan2(Math.sqrt(variableA), Math.sqrt(1-variableA));
                            double shortestWeight = EARTH_RADIUS_KM *variableC;

                            if(duration!=null && type == OSMWayType.FERRY) {
                                String splitTime[]=duration.split(":");
                                double hours;
                                double minutes;
                                if(splitTime.length < 2){
                                    hours = 0;
                                    minutes= Double.parseDouble(splitTime[0]);
                                } else {
                                    hours = Double.parseDouble(splitTime[0]);
                                    minutes= Double.parseDouble(splitTime[1]);
                                }
                                short totalHours =  (short) (Math.ceil(hours + minutes/60));
                                speed = (short) (Math.ceil(shortestWeight/totalHours));
                            }
                            EdgeWeightedDigraph.setSize(arrayId);
                            if (oneWay){
                                EdgeWeightedDigraph.addEdge(
                                        start,
                                        end,
                                        (float) shortestWeight,
                                        speed,
                                        walkAllowed,
                                        bikeAllowed,
                                        carAllowed,
                                        roadName);
                            }
                            else {
                                EdgeWeightedDigraph.addBidirectionalEdge(
                                        start,
                                        end,
                                        (float) shortestWeight,
                                        speed,
                                        walkAllowed,
                                        bikeAllowed,
                                        carAllowed,
                                        roadName);
                            }
                        }
                        NamedShapes namedShape = new NamedShapes(path, roadName);
                        namedShape.addPoint(way.get(0).getLat(), way.get(0).getLon(), way.get(0).getArrayId());
                        node = way.get(0);
                        path.moveTo(node.getLon(), node.getLat());
                        for (int i = 1; i < way.size(); i++) {
                            node = way.get(i);
                            namedShape.addPoint(way.get(i).getLat(), way.get(i).getLon(), way.get(i).getArrayId());
                            path.lineTo(node.getLon(), node.getLat());
                        }
                        if(roadName!=null)add(type, namedShape);
                        else add(type, path);

                    }
                    way = null;
                    oneWay =false;
                    bikeAllowed = false;
                    walkAllowed = false;
                    carAllowed = false;
                    roadName = null;
                    speedLimit=null;
                    duration = null;
                    break;
                case "relation":
                    for (OSMWay way: relation) {
                        node = way.get(0);
                        path.moveTo(node.getLon(), node.getLat());
                        for (int i = 1; i < way.size(); i++) {
                            node = way.get(i);
                            path.lineTo(node.getLon(), node.getLat());
                        }
                    }

                    add(type, path);
                    break;
                case "osm":
                    for(Map.Entry<OSMNode,OSMWay> coastline: coastlines.entrySet()){
                        OSMWay way = coastline.getValue();
                        if(coastline.getKey() == way.from()){
                            path = new Path2D.Float();
                            path.setWindingRule(Path2D.WIND_EVEN_ODD);
                            node = way.get(0);
                            path.moveTo(node.getLon(), node.getLat());
                            for (int i = 1; i < way.size(); i++) {
                                node = way.get(i);
                                path.lineTo(node.getLon(), node.getLat());
                            }
                            NamedShapes namedShape1 = new NamedShapes(path, null);
                            add(OSMWayType.COASTLINE, namedShape1);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Returns the list of OSMAddresses.
     * @return OSMAddresses.
     */
    public ArrayList<OSMAddress> getOSMAddresses() {
        Collections.sort(OSMAddresses);
        return OSMAddresses;
    }

}