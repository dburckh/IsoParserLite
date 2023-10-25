package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageSpatialExtentsReader extends FullBoxReader implements TypedParser {
    @Override
    public ImageSpatialExtents read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final ByteBuffer byteBuffer = StreamUtil.getByteBuffer(box.getPayloadSize(true), streamReader);
        return new ImageSpatialExtents(byteBuffer.getInt(), byteBuffer.getInt());
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_ispe;
    }
}
