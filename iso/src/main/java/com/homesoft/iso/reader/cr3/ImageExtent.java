package com.homesoft.iso.reader.cr3;

import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.reader.Extent;

import java.util.Comparator;

public class ImageExtent extends Extent {
    public static final Comparator<ImageExtent> COMPARATOR = (i0, i1) -> {
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
    private final short type;
    private final short width;
    private final short height;
    public ImageExtent(short type, short width, short height, long offset, int len) {
        super(offset, len);
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width & StreamUtil.USHORT_MASK;
    }

    public int getHeight() {
        return height & StreamUtil.USHORT_MASK;
    }

    public short getType() {
        return type;
    }

    @Override
    protected String toStringPrefix() {
        return super.toStringPrefix() +
                ", type=" + type +
                ", width=" + width +
                ", height=" + height;
    }
}
