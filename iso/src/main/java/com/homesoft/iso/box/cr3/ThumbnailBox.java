package com.homesoft.iso.box.cr3;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;

public class ThumbnailBox implements TypedBox {
    public static final int TYPE_THMB = 0x54484D42;
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Nullable
    @Override
    public JpegImage read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = BoxHeader.getVersion(versionFlags);
        return new JpegImage(streamReader.getShort(),
                streamReader.getShort(),
                version == 1 ? streamReader.position() : streamReader.position() + 4,
                streamReader.getInt());
    }

    @Override
    public int getType() {
        return TYPE_THMB;
    }
}
