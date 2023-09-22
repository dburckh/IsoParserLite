package com.homesoft.iso.parser;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.Type;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Contain struct as defined here (@see HevcDecoderConfigurationRecord::parseConfig) + codecConfig
 *
 * https://github.com/nokiatech/heif/blob/master/srcs/common/hevcdecoderconfigrecord.cpp
 */
public class HevcDecoderConfig implements Type, CodecSpecificData {
    private final byte[] bytes;
    public HevcDecoderConfig(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Get the Codec Config in Byte Stream Format (H265 Appendix B)
     */
    public ByteBuffer getCSDByteBuffer() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        // Skip the header
        byteBuffer.position(0x16);
        final ByteBuffer codecConfig = ByteBuffer.allocate(bytes.length - 0x1a);
        final int count = DataUtil.getUByte(byteBuffer);
        for (int i=0;i<count;i++) {
            byteBuffer.get(); // nalUntType
            byteBuffer.getShort(); //numNalus should be 1
            final int nalSize = DataUtil.getUShort(byteBuffer);
            // Add the 0x00000001 for byte stream format
            codecConfig.putInt(1);
            byteBuffer.limit(byteBuffer.position() + nalSize);
            codecConfig.put(byteBuffer);
            byteBuffer.limit(bytes.length);
        }
        codecConfig.clear();
        return codecConfig;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_hvcC;
    }
}
