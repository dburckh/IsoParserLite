package com.homesoft.iso.box.cr3;

import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.box.Extent;

import java.util.Comparator;

public class JpegImage extends Extent {
    public static final Comparator<JpegImage> COMPARATOR = (i0, i1) -> {
        if (i0 == i1) {
            return 0;
        } else if (i0 == null) {
            return -1;
        } else if (i1 == null) {
            return 1;
        }
        int rc = Integer.compare(i0.getWidth(), i1.getWidth());
        if (rc == 0) {
            return Long.compare(i0.size, i1.size);
        }
        return rc;
    };
    private final short width;
    private final short height;
    public JpegImage(short width, short height, long offset, int len) {
        super(len, offset);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width & StreamUtil.USHORT_MASK;
    }

    public int getHeight() {
        return height & StreamUtil.USHORT_MASK;
    }

    @Override
    protected String toStringPrefix() {
        return super.toStringPrefix() + ", width=" + width +
                ", height=" + height;
    }
}
