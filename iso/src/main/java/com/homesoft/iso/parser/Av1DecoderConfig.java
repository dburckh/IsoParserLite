package com.homesoft.iso.parser;

import java.nio.ByteBuffer;

/**
 * Contents of av1C box
 * Consist of a header and CodecSpecificData
 */
public class Av1DecoderConfig implements CodecSpecificData {
    private final byte[] bytes;
    public Av1DecoderConfig(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public ByteBuffer getCSDByteBuffer() {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length - 4);
        byteBuffer.put(bytes, 4, byteBuffer.capacity());
        return byteBuffer;
    }
}
