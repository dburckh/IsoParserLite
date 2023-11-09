package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.DependantBoxReader;
import com.homesoft.iso.DependencyManager;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.Movie;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;

import java.io.IOException;
import java.nio.ByteBuffer;

public class TrackHeaderReader implements TypedParser, DependantBoxReader {
    private static final int SKIP = 8 + // reserved
        2 + // layer
        2 + // alternateGroup
        2 + // volume
        2; // reserved

    private MovieHeaderReader movieHeaderReader;

    public TrackHeaderReader(DependencyManager dependencyManager, MovieHeaderReader movieHeaderReader) {
        this.movieHeaderReader = movieHeaderReader;
        dependencyManager.add(movieHeaderReader);
    }

    @Override
    public TrackHeader read(Box box, StreamReader streamReader, Object dependency) throws IOException {
        int versionFlags = streamReader.getInt();
        final int version = Box.getVersion(versionFlags);
        final long creationTime, modificationTime, duration;
        final int trackId;
        final ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(box.getPayloadSize(true), streamReader);
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
        byte[] matrixBytes = new byte[9 * 4];
        byteBuffer.get(matrixBytes);
        final float width = Movie.toFloat(byteBuffer.getInt());
        final float height = Movie.toFloat(byteBuffer.getInt());
        Header movieHeader = (Header) dependency;
        return new TrackHeader(creationTime, modificationTime, movieHeader.getTimescale(),
                duration, trackId, matrixBytes, width, height);
    }

    @Override
    public BoxReader getDependantParser() {
        return movieHeaderReader;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_tkhd;
    }
}
