package KdTree;

import Model.OSM.OSMWayType;
import View.WindowView;

import java.awt.*;
import java.util.*;
import java.util.List;

import static View.WindowView.debugPrintln;

/**
 * This class creates a KdTree
 */
public class KdTreeFactory implements Runnable{
    private OSMWayType key;
    private ArrayList<Shape> shapes;
    private static final short CUTOFF = 1000; //Perhaps 1000

    /**
     * Creates a KdTreeFactory that can create KdTrees
     * @param key The OSMWayType of the KdTree to be created
     * @param shapes The list of shapes to be put in the KdTree
     */
    KdTreeFactory(OSMWayType key, List<Shape> shapes){
        this.key=key;
        this.shapes= (ArrayList<Shape>) shapes;

    }

    /**
     * This method runs the method to create the KdTree and add the KdTree to the threadKeeper for faster performance.
     */
    public void run(){
        KdTree tree = createTree(shapes);
        KdTreeThreadKeeper.addTree(key, tree);
        debugPrintln(key + " finished");
        shapes=null;
    }

    /**
     * This method creates the KdTree by making subtrees of the root
     * @param list The list of shapes to be put in the KdTree
     * @return The KdTree
     */
    public KdTree createTree(ArrayList<Shape> list){
        KdTree kdTree = new KdTree();
        Node root = kdTree.getRoot();
        makeSubTree(root, list.size()-1,0);
        return kdTree;
    }

    /**
     * This method recursively makes subtrees of the given node until the CUTOFF value is reached. Then it creates leafs,
     * that contains the sublist of shapes
     * @param node The root node of the subtree
     * @param hi The upper index boundary of the sublist
     * @param lo The lower index boundary of the sublist
     */
    private void makeSubTree(Node node, int hi, int lo){
        //medianIndex and value gets added to the node
        addValue(node, hi, lo);

        if(!(hi - lo <= CUTOFF)){
            // Leftchild and rightChild gets connected to the rootNode
            Node leftNode = new Node();
            node.setLeftChild(leftNode);
            Node rightNode = new Node();
            node.setRightChild(rightNode);

            leftNode.setDepth(node.getDepth()+1);
            rightNode.setDepth(node.getDepth()+1);
            makeSubTree(leftNode,node.getMedianIndex(),lo);
            makeSubTree(rightNode,hi,node.getMedianIndex());
        } else {
            Leaf leftLeaf = new Leaf(shapes.subList(lo,node.getMedianIndex()+1));
            node.setLeftChild(leftLeaf);

            Leaf rightLeaf = new Leaf(shapes.subList(node.getMedianIndex(),hi+1));
            node.setRightChild(rightLeaf);
        }
    }

    /**
     * This method checks if the KdTree node value is a vertical boundary by using the depth of the node.
     * @param node The node to be checked
     * @return True if the node contains a vertical boundary
     */
    private boolean lineIsVertical(Node node){ //this method is called at the end of the creation of a node
        return node.getDepth()%2==0;
    }

    /**
     * This method sorts the list of shapes either by x coordinates or y coordinates depending on whether the node gives a vertical
     * or horizontal boundary, and finds the median index of the list and the value in the median index, and sets them in the node.
     * @param node The node to be set median index and value in
     * @param hi The upper boundary of the sublist
     * @param lo The lower boundary of the sublist
     */
    private void addValue(Node node, int hi, int lo){ //skal kontrollere fokusområdet baseret på nodes
        sort(shapes,lo,hi,lineIsVertical(node));
        int medianIndex = Math.floorDiv(hi - lo,2) + lo;
        if (lineIsVertical(node)) node.setValue(shapes.get(medianIndex).getBounds2D().getCenterX());
        else node.setValue(shapes.get(medianIndex).getBounds2D().getCenterY());
        node.setMedianIndex(medianIndex);
    }

    /**
     * Iterative Quick Sort
     * By Aashish Barnwal and reviewed by GeeksforGeeks team
     * @param shapes The list of shapes to be sorted
     * @param lo The lower index boundary of the sort
     * @param hi The upper index boundary of the sort
     * @param isVertical A check on whether the shapes should be sorted on y or x values
     */
    private void sort(ArrayList<Shape> shapes, int lo, int hi, boolean isVertical) {
        if (hi <= lo) return;

        // create auxiliary stack
        int stack[] = new int[hi-lo+1];

        // initialize top of stack
        int top = -1;

        // push initial values in the stack
        stack[++top] = lo;
        stack[++top] = hi;

        // keep popping elements until stack is not empty
        while (top >= 0)
        {
            // pop h and l
            hi = stack[top--];
            lo = stack[top--];

            // set pivot element at it's proper position
            int p = partition(shapes, lo, hi,isVertical);

            // If there are elements on left side of pivot,
            // then push left side to stack
            if ( p-1 > lo )
            {
                stack[ ++top ] = lo;
                stack[ ++top ] = p - 1;
            }

            // If there are elements on right side of pivot,
            // then push right side to stack
            if ( p+1 < hi )
            {
                stack[ ++top ] = p + 1;
                stack[ ++top ] = hi;
            }
        }

    }

    /**
     * The partition method in quicksort
     * Source: Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne
     * @param shapes The list of shapes to be sorted
     * @param lo The lower index boundary of the sort
     * @param hi The upper index boundary of the sort
     * @param isVertical A check on whether the shapes should be sorted on y or x values
     * @return The index of the pivot element
     * @author Robert Sedgewick (from
     * @author Kevin Wayne
     */
    private int partition(ArrayList<Shape> shapes, int lo, int hi, boolean isVertical) {
        int i = lo;
        int j = hi + 1;
        Shape compareShape = shapes.get(lo);
        while (true) {

            // find item on lo to swap based on x
            if(isVertical) {
                while (less(shapes.get(++i).getBounds2D().getCenterX(), compareShape.getBounds2D().getCenterX())) {
                    if (i == hi) break;
                }

                // find item on hi to swap based on y
                while (less(compareShape.getBounds2D().getCenterX(), shapes.get(--j).getBounds2D().getCenterX())) {
                    if (j == lo) break;      // redundant since a[lo] acts as sentinel
                }
            } else {
                while (less(shapes.get(++i).getBounds2D().getCenterY(), compareShape.getBounds2D().getCenterY())) {
                    if (i == hi) break;
                }

                // find item on hi to swap
                while (less(compareShape.getBounds2D().getCenterY(), shapes.get(--j).getBounds2D().getCenterY())) {
                    if (j == lo) break;      // redundant since a[lo] acts as sentinel
                }
            }
            // check if pointers cross
            if (i >= j) break;

            exch(shapes, i, j);
        }

        // put partitioning item v at a[j]
        exch(shapes, lo, j);

        // now, a[lo .. j-1] <= a[j] <= a[j+1 .. hi]
        return j;
    }

    /**
     * Method checks if v < w
     * Source: Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne
     * @param v Coordinates to be compared
     * @param w Coordinates to be compared
     * @return True if it less
     * @author Robert Sedgewick (from
     * @author Kevin Wayne
     */
    private static boolean less(Comparable v, Comparable w) {
        if (v == w) return false;
        return v.compareTo(w) < 0;
    }

    /**
     * The method exchanges the position of shapes in the list
     * @param list The list of shapes
     * @param i The index of the first shape
     * @param j The index of the second shape
     */
    private static void exch(ArrayList<Shape> list, int i, int j) {
        Shape swap = list.get(i);
        list.set(i,list.get(j));
        list.set(j,swap);
    }
}