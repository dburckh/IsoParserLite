package com.homesoft.iso.box.cr3;

import com.homesoft.iso.box.Extent;

public class JpegImage extends Extent {
    public final short width;
    public final short height;
    public JpegImage(short width, short height, int len, long offset) {
        super(len, offset);
        this.width = width;
        this.height = height;
    }

    @Override
    protected String toStringPrefix() {
        return super.toStringPrefix() + ", width=" + width +
                ", height=" + height;
    }
}
