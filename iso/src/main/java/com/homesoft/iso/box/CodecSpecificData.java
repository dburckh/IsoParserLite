package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Return the CodecConfig data
 */
public interface CodecSpecificData {
    byte TYPE_NA = 0;

    List<Data> getCodecSpecificData();

    class Data {
        public final byte type;
        public final ByteBuffer byteBuffer;

        public Data(byte type, ByteBuffer byteBuffer) {
            this.type = type;
            this.byteBuffer = byteBuffer;
        }

        @Nullable
        public static ByteBuffer findType(final byte type, List<Data> list) {
            for (Data data : list) {
                if (type == data.type) {
                    return data.byteBuffer;
                }
            }
            return null;
        }
    }
}
