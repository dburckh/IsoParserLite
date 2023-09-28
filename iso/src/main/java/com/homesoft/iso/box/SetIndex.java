package com.homesoft.iso.box;

/**
 * Track & Disk from iTunes
 */
public class SetIndex {
    /**
     * Index of this item in the set starting at 1.
     * For example: This is the track number
     */
    public final int index;
    /**
     * Total size of the set.
     * For example: Total tracks on the album
     */
    public final int size;

    public SetIndex(int index, int size) {
        this.index = index;
        this.size = size;
    }

    @Override
    public String toString() {
        return index + " of " + size;
    }
}
