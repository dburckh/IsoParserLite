package com.homesoft.iso;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Contains the type and size of a box.
 */
public class Box {
    /**
     * Indicates the box goes to the end of the file (stream)
     */
    public static final long SIZE_EOF = 0;
    private final Number size;
    public final int type;

    public static int getVersion(int versionFlags) {
        return versionFlags >> 24;
    }

    public static int getFlags(int versionFlags) {
        return versionFlags & 0xffffff;
    }

    /**
     *
     * @throws BufferUnderflowException thrown if there aren't enough bytes in the stream
     */
    @NonNull
    public static Box readBox(StreamReader streamReader) throws IOException, BufferUnderflowException {
        ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(8, streamReader);
        Number size = byteBuffer.getInt();
        final int type = byteBuffer.getInt();
        if (size.intValue() == 1) {
            byteBuffer = StreamUtil.requireSharedBuffer(8, streamReader);
            size = byteBuffer.getLong();
        }
        if (type == BoxTypes.TYPE_uuid) {
            byteBuffer = StreamUtil.requireSharedBuffer(16, streamReader);
            byte[] uuid = new byte[UUIDBox.UUID_SIZE];
            byteBuffer.get(uuid);
            return new UUIDBox(size, type, uuid);
        } else {
            return new Box(size, type);
        }
    }

    public Box(Number size, int type) {
        this.size = size;
        this.type = type;
    }

    /**
     * Get the box size as a long or SIZE_EOF
     */
    public long getSize() {
        if (size instanceof Long) {
            return size.longValue();
        } else {
            return StreamUtil.getUInt(size.intValue());
        }
    }

    public int getHeaderSize(boolean fullBox) {
        int headerSize = size instanceof Long ? 16 : 8;
        if (fullBox) {
            return headerSize + 4;
        } else {
            return headerSize;
        }
    }

    /**
     * Calculate size of the payload (data after header and flags)
     * @param fullBox Whether this box is a {@link BoxReader#isFullBox()}
     */
    public long getPayloadSize(boolean fullBox) {
        long size = getSize();
        if (size == 0) {
            throw new UnsupportedOperationException("Box size is EOF");
        }
        return size - getHeaderSize(fullBox);
    }

    /**
     * Convert the int {@link Type} to a String
     */
    public static StringBuilder typeToString(int type) {
        final StringBuilder sb = new StringBuilder();
        for (int i=0;i<4;i++) {
            sb.append((char)(type&0xff));
            type >>=8;
        }
        sb.reverse();
        return sb;
    }

    @Override
    public String toString() {
        return "Box{size="+getSize()+", type="+typeToString(type)+"}";
    }
}
