package com.homesoft.iso.reader;

import com.homesoft.iso.StreamUtil;

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
    public final int size;

    public Extent(final long offset, final long size) {
        this(offset, (int)size);
        if (size >= (1L << 32)) {
            throw new IllegalArgumentException("Size not supported: " + size);
        }
    }
    public Extent(final long offset, final int size) {
        this.offset = offset;
        this.size = size;
    }

    public long getEnd() {
        return offset + size;
    }

    public long getSizeUInt() {
        return StreamUtil.getUInt(size);
    }

    protected String toStringPrefix() {
        return getClass().getSimpleName()+ "{offset="+offset+", size=" + size;
    }

    @Override
    public String toString() {
        return toStringPrefix() + '}';
    }
}
