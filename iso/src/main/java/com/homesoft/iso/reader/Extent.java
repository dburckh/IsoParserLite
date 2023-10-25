package com.homesoft.iso.reader;

/**
 * A subsection of a file or stream
 */
public class Extent {
    /**
     * Starting offset of the item aka position
     */
    public final long offset;
    /**
     * Size or length of the item in bytes
     */
    public final long size;

    public Extent(final long offset, final long size) {
        this.offset = offset;
        this.size = size;
    }

    protected String toStringPrefix() {
        return getClass().getSimpleName()+ "{offset="+offset+", size=" + size;
    }

    @Override
    public String toString() {
        return toStringPrefix() + '}';
    }
}