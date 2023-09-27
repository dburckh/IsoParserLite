package com.homesoft.iso.box;

import com.homesoft.iso.DataUtil;

public class PixelAspectRatio {
    private final int hSpacing;
    private final int vSpacing;

    public PixelAspectRatio(int hSpacing, int vSpacing) {
        this.hSpacing = hSpacing;
        this.vSpacing = vSpacing;
    }

    public long getHSpacing() {
        return DataUtil.getUInt(hSpacing);
    }

    public long getVSpacing() {
        return DataUtil.getUInt(vSpacing);
    }
}
