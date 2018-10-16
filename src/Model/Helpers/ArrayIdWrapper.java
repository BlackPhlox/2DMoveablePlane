package Model.Helpers;

import Model.Pathfinding;

import java.util.HashMap;

/**
 * Array id of nodes is wrapped to include to a compare method based on distTo to be used in Pathfinding.
 */
public class ArrayIdWrapper implements Comparable<ArrayIdWrapper> {
    private int arrayId;

    /**
     * Constructor set the array id.
     * @param arrayId Uses arrayId to set to arrayId.
     */
    public ArrayIdWrapper(int arrayId) {
        this.arrayId = arrayId;

    }

    /**
     * Compare to.
     * @param a ArrayIdWrapper used to get the array id.
     * @return Either, 1, -1 or 0.
     */
    @Override
    public int compareTo(ArrayIdWrapper a) {
        HashMap<Integer,Float> distTo = Pathfinding.getDistTo();
        if (distTo.get(arrayId) > distTo.get(a.getArrayId())) return 1;
        if (distTo.get(arrayId) < distTo.get(a.getArrayId())) return -1;
        return 0;
    }

    /**
     * Get array id.
     * @return The arrayId.
     */
    public int getArrayId() {
        return arrayId;
    }
}
