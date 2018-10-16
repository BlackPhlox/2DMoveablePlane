package KdTree;

import Model.OSM.OSMWayType;
import View.WindowView;

import java.awt.*;
import java.util.*;
import java.util.List;

import static View.WindowView.debugPrintln;

/**
 * This class using threading to create KdTrees of each OSMWayType at the same time
 */
public class KdTreeThreadKeeper {
    private static Map<OSMWayType, KdTree> kdMap;

    /**
     * For every entry in the EnumMap, a KdTree is created.
     * @param enumMap The map with all the shapes of different OSMWayTypes
     */
    public KdTreeThreadKeeper(EnumMap<OSMWayType,java.util.List<Shape>> enumMap){
        kdMap = Collections.synchronizedMap(new EnumMap<OSMWayType, KdTree>(OSMWayType.class));
        Thread[] threads = new Thread[OSMWayType.values().length];
        int i = 0;
        for (Map.Entry<OSMWayType, List<Shape>> entry : enumMap.entrySet()) {
            OSMWayType key = entry.getKey();
            debugPrintln(key.toString());
            ArrayList<Shape> shapes = (ArrayList<Shape>) entry.getValue();
            if(shapes.size()!=0){
                debugPrintln(key + " is being created");
                Collections.shuffle(shapes);
                threads[i] = new Thread(new KdTreeFactory(key, shapes));
                threads[i].start();
            } else {
                threads[i] = new Thread();
                threads[i].start();
            }
            i++;
        }

        for (Thread t : threads) {
            try {
                if(t!=null)t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Threads done");
    }

    /**
     * This method adds the KdTree created to the kdMap
     * @param key The OSMWayType
     * @param tree The KdTree
     */
    public static void addTree(OSMWayType key, KdTree tree){
        kdMap.put(key,tree);
    }

    /**
     * Returns the kdMap
     * @return The kdMap
     */
    public Map<OSMWayType,KdTree> getKdMap() {
        return kdMap;
    }

    /**
     * This method sets the kdMap to null
     */
    public static void nullStaticResult(){
        kdMap=null;
    }

}