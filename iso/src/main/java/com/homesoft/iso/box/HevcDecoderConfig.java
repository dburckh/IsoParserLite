package com.homesoft.iso.box;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.Type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contain struct as defined here (@see HevcDecoderConfigurationRecord::parseConfig) + codecConfig
 *
 * <a href="https://github.com/nokiatech/heif/blob/master/srcs/common/hevcdecoderconfigrecord.cpp">...</a>
 */
public class HevcDecoderConfig implements Type, CodecSpecificData {
    public static final byte TYPE_VPS = 32;
    public static final byte TYPE_SPS = 33;
    public static final byte TYPE_PPS = 34;
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
    public List<TypedConfig> getCodecSpecificData() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).asReadOnlyBuffer();
        // Skip the header
        byteBuffer.position(0x16);
        final int count = StreamUtil.getUByte(byteBuffer);
        final ArrayList<TypedConfig> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final int type = byteBuffer.get() & 0x3f; // nalUntType
            final int naluCount = StreamUtil.getUShort(byteBuffer.getShort()); //numNalus should be 1
            for (int n = 0; n < naluCount; n++) {
                final int nalSize = StreamUtil.getUShort(byteBuffer);
                byteBuffer.limit(byteBuffer.position() + nalSize);
                final ByteBuffer codecBuffer = byteBuffer.slice();
                list.add(new TypedConfig((byte)type, codecBuffer));
                byteBuffer.position(byteBuffer.limit());
                byteBuffer.limit(bytes.length);
            }
        }
        return list;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_hvcC;
    }
}
