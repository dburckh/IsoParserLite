package com.homesoft.iso.parser;

import com.homesoft.iso.Type;

import java.nio.ByteBuffer;

/**
 * Generic wrapper class for a byte[] buffer.
 */
public class BytesType implements Type {
    public final int type;
    private final byte[] bytes;
    public BytesType(int type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }

    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(bytes);
    }
    @Override
    public int getType() {
        return type;
    }
}
