package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Return the CodecConfig data
 */
public interface CodecSpecificData {
    byte TYPE_NA = 0;

    List<TypedConfig> getCodecSpecificData();

    class TypedConfig {
        public final byte type;
        public final ByteBuffer byteBuffer;

        public TypedConfig(byte type, ByteBuffer byteBuffer) {
            this.type = type;
            this.byteBuffer = byteBuffer;
        }

        @Nullable
        public static ByteBuffer findType(final byte type, List<TypedConfig> list) {
            for (TypedConfig typedConfig : list) {
                if (type == typedConfig.type) {
                    return typedConfig.byteBuffer;
                }
            }
            return null;
        }
    }
}
