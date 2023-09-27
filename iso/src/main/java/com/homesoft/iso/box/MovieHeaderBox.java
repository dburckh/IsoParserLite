package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MovieHeaderBox implements TypedBox {
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Override
    public Header read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = BoxHeader.getVersion(versionFlags);
        final long creationTime, modificationTime, duration;
        final int timescale;
        final ByteBuffer byteBuffer = DataUtil.requireSharedBuffer(boxHeader.getPayloadSize(isFullBox()), streamReader);
        if (version == 1) {
            creationTime = byteBuffer.getLong();
            modificationTime = byteBuffer.getLong();
            timescale = byteBuffer.getInt();
            duration = byteBuffer.getLong();
        } else if (version == 0) {
            creationTime = DataUtil.getUInt(byteBuffer);
            modificationTime = DataUtil.getUInt(byteBuffer);
            timescale = byteBuffer.getInt();
            duration = DataUtil.getUInt(byteBuffer);
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
