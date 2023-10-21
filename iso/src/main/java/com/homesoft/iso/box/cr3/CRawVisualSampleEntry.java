package com.homesoft.iso.box.cr3;

import com.homesoft.iso.box.VisualSampleEntry;

public class CRawVisualSampleEntry extends VisualSampleEntry {
    public static final short IMAGE_TYPE_RAW = 1;
    public static final short IMAGE_TYPE_JPEG = 3;

    private final short imageType;

    public CRawVisualSampleEntry(VisualSampleEntry visualSampleEntry, short imageType) {
        super(visualSampleEntry);
        this.imageType = imageType;
    }

    public short getImageType() {
        return imageType;
    }

    @Override
    public String toStringPrefix() {
        return super.toStringPrefix() + ", imageType=" + imageType;
    }
}
