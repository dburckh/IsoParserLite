package com.homesoft.iso.box;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

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
    public List<TypedConfig> getCodecSpecificData() {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length - 4);
        byteBuffer.put(bytes, 4, byteBuffer.capacity());
        return Collections.singletonList(new TypedConfig(TYPE_NA, byteBuffer.asReadOnlyBuffer()));
    }
}
