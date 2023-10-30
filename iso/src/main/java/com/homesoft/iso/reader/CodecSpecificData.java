package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Return the CodecConfig data
 */
public interface CodecSpecificData {
    /**
     * Type used for codec specific data that has no type
     */
    byte TYPE_NA = 0;

    /**
     * Ordered List of {@link TypedConfig} order is determined by the instance.
     * There may be multiples of the same {@link TypedConfig#type}, but it's unlikely.
     */
    List<TypedConfig> getTypedConfigList();

    class TypedConfig {
        /**
         * Codec specific config type
         */
        public final byte type;
        /**
         * ByteBuffer containing the codec specific data.  Should be considered readOnly.
         */
        public final ByteBuffer byteBuffer;

        public TypedConfig(byte type, ByteBuffer byteBuffer) {
            this.type = type;
            this.byteBuffer = byteBuffer;
        }

        /**
         * Find the first instance of a given type
         * @param type {@link TypedConfig#type} to seek
         * @param list List from {@link CodecSpecificData#getTypedConfigList()}
         * @return the first {@link TypedConfig#byteBuffer} of the given type
         */
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
