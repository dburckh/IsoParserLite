package com.homesoft.iso.parser;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.Type;

public class ImageSpatialExtents implements Type {
    public final int width;
    public final int height;

    public ImageSpatialExtents(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_ispe;
    }

    @Override
    public String toString() {
        return "ImageSpatialExtents{width=" + width + ", height=" + height + "}";
    }
}
