package com.homesoft.iso.box;

import com.homesoft.iso.StreamUtil;

public class VisualSampleEntry extends SampleEntry {
    private final short width;
    private final short height;

    public VisualSampleEntry(VisualSampleEntry visualSampleEntry) {
        this(visualSampleEntry, visualSampleEntry.width, visualSampleEntry.height);
    }

    public VisualSampleEntry(SampleEntry sampleEntry, short width, short height) {
        super(sampleEntry);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return StreamUtil.getUShort(width);
    }

    public short getRawWidth() {
        return width;
    }

    public int getHeight() {
        return StreamUtil.getUShort(height);
    }

    public short getRawHeight() {
        return height;
    }

    @Override
    public String toStringPrefix() {
        return super.toStringPrefix() +
                ", width=" + width +
                ", height=" + height ;
    }
}
