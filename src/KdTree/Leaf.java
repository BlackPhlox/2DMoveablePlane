package KdTree;

import Model.OSM.NamedShapes;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class describes the leafs of the KdTree, which is a set of shapes instead of a Node.
 */
public class Leaf extends Node implements Serializable {
    private Set<Shape> leaf;

    /**
     * A hashset of NamedShapes is created to avoid duplication
     * @param list The list to be made into a hashSet
     */
    public Leaf(List<Shape> list){
        leaf = new HashSet<>(list);
    }

    /**
     * Returns the leaf
     * @return The Leaf
     */
    public Set getLeaf() {
        return leaf;
    }
}