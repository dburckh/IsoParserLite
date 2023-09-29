package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageSpatialExtentsBox implements TypedBox {
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Override
    public ImageSpatialExtents read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final ByteBuffer byteBuffer = StreamUtil.getByteBuffer(boxHeader.getPayloadSize(isFullBox()), streamReader);
        return new ImageSpatialExtents(byteBuffer.getInt(), byteBuffer.getInt());
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_ispe;
    }
}
