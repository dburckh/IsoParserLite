package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MovieHeaderReader extends FullBoxReader implements TypedParser {
    @Override
    public Header read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = Box.getVersion(versionFlags);
        final long creationTime, modificationTime, duration;
        final int timescale;
        final ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(box.getPayloadSize(true), streamReader);
        if (version == 1) {
            creationTime = byteBuffer.getLong();
            modificationTime = byteBuffer.getLong();
            timescale = byteBuffer.getInt();
            duration = byteBuffer.getLong();
        } else if (version == 0) {
            creationTime = StreamUtil.getUInt(byteBuffer);
            modificationTime = StreamUtil.getUInt(byteBuffer);
            timescale = byteBuffer.getInt();
            duration = StreamUtil.getUInt(byteBuffer);
        } else {
            return null;
        }
        return new Header(creationTime, modificationTime, timescale, duration);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_mvhd;
    }
}
