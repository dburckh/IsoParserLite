package com.homesoft.iso.box;


import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaHeaderBox implements TypedBox {
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Override
    public MediaHeader read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = BoxHeader.getVersion(versionFlags);
        final long creationTime, modificationTime, duration;
        final int timescale;
        final ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(boxHeader.getPayloadSize(isFullBox()), streamReader);
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
        final short language = byteBuffer.getShort();
        return new MediaHeader(creationTime, modificationTime, timescale, duration, language);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_mdhd;
    }
}
