package com.homesoft.iso.box;

import com.homesoft.iso.DataUtil;

public class VisualSampleEntry extends SampleEntry {
    private final short width;
    private final short height;

    public VisualSampleEntry(SampleEntry sampleEntry, short width, short height) {
        super(sampleEntry);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return DataUtil.getUShort(width);
    }

    public int getHeight() {
        return DataUtil.getUShort(height);
    }

    @Override
    public String toString() {
        return toStringPrefix() +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
