package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.Media;
import com.homesoft.iso.ResultResolver;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TrackHeaderBox implements TypedBox {
    private static final int SKIP = 8 + // reserved
        2 + // layer
        2 + // alternateGroup
        2 + // volume
        2 + // reserved
        9 * 4; // matrix

    private final ResultResolver resultResolver;

    public TrackHeaderBox(ResultResolver resultResolver) {
        this.resultResolver = resultResolver;
    }
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Override
    public TrackHeader read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = BoxHeader.getVersion(versionFlags);
        final long creationTime, modificationTime, duration;
        final int trackId;
        final ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(boxHeader.getPayloadSize(isFullBox()), streamReader);
        if (version == 1) {
            creationTime = byteBuffer.getLong();
            modificationTime = byteBuffer.getLong();
            trackId = byteBuffer.getInt();
            byteBuffer.getInt(); // Reserved
            duration = byteBuffer.getLong();
        } else if (version == 0) {
            creationTime = StreamUtil.getUInt(byteBuffer);
            modificationTime = StreamUtil.getUInt(byteBuffer);
            trackId = byteBuffer.getInt();
            byteBuffer.getInt(); // Reserved
            duration = StreamUtil.getUInt(byteBuffer);
        } else {
            return null;
        }
        byteBuffer.position(byteBuffer.position() + SKIP);
        final float width = Media.toFloat(byteBuffer.getInt());
        final float height = Media.toFloat(byteBuffer.getInt());
        final Header header = (Header) resultResolver.getResult(BoxTypes.TYPE_mvhd);
        return new TrackHeader(creationTime, modificationTime, header.getTimescale(),
                duration, trackId, width, height);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_tkhd;
    }
}
