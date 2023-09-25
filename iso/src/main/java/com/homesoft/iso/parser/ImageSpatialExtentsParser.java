package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBoxParser;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageSpatialExtentsParser implements TypedBoxParser {
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Override
    public ImageSpatialExtents parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final ByteBuffer byteBuffer = DataUtil.getByteBuffer(box.getPayloadSize(isFullBox()), streamReader);
        return new ImageSpatialExtents(byteBuffer.getInt(), byteBuffer.getInt());
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_ispe;
    }
}
