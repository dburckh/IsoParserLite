package com.homesoft.iso.parser;

public class Extent {
    public final long offset;
    public final long size;

    public Extent(final long offset, final long size) {
        this.offset = offset;
        this.size = size;
    }

    @Override
    public String toString() {
        return "Extent{offset="+offset+", size=" + size+"}";
    }
}
