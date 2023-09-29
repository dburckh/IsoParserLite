package com.homesoft.iso.box;

import com.homesoft.iso.StreamUtil;

public class PixelAspectRatio {
    private final int hSpacing;
    private final int vSpacing;

    public PixelAspectRatio(int hSpacing, int vSpacing) {
        this.hSpacing = hSpacing;
        this.vSpacing = vSpacing;
    }

    public long getHSpacing() {
        return StreamUtil.getUInt(hSpacing);
    }

    public long getVSpacing() {
        return StreamUtil.getUInt(vSpacing);
    }
}
