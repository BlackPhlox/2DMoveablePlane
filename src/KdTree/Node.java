package KdTree;

import java.io.Serializable;

/**
 * This class describes the nodes in the KdTree, with the necessary getter and setter methods.
 * Every node has a leftChild, rightChild, depth (which is used to calculate if the value is a horizontal or vertical boundary),
 * the medianIndex and the value associated with that index.
 */
public class Node implements Serializable {
    private Node leftChild, rightChild;
    private int depth, medianIndex;
    private double value;

    /**
     * The constructor for this class i empty.
     */
    Node(){
    }

    /**
     * Get left child.
     * @return The leftChild.
     */
    public Node getLeftChild() {
        return leftChild;
    }

    /**
     * Get right child.
     * @return The rightChild.
     */
    public Node getRightChild() {
        return rightChild;
    }

    /**
     * Get the depth.
     * @return The depth.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Get the value.
     * @return The value.
     */
    public double getValue() {
        return value;
    }

    /**
     * Checks if vertical.
     * @return The boolean for if it is vertical.
     */
    public boolean isVertical(){ //this method is called at the end of the creation of a node
        return getDepth()%2==0;
    }

    /**
     * Get median index.
     * @return The medianIndex.
     */
    public int getMedianIndex() {
        return medianIndex;
    }

    /**
     * Set left child.
     * @param leftChild The leftChild.
     */
    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    /**
     * Set right child.
     * @param rightChild The rightChild.
     */
    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    /**
     * Set the depth.
     * @param depth Uses depth to set the depth.
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Set the value.
     * @param value Uses value to set the value.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Set median index.
     * @param medianIndex Uses medianIndex to set medianIndex.
     */
    public void setMedianIndex(int medianIndex) {
        this.medianIndex = medianIndex;
    }
}