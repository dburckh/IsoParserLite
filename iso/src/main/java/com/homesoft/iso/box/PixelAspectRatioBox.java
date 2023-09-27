package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;

public class PixelAspectRatioBox implements TypedBox {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public PixelAspectRatio read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        return new PixelAspectRatio(streamReader.getInt(), streamReader.getInt());
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_pasp;
    }
}
