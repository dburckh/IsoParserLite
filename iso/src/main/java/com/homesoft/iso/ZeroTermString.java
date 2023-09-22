package com.homesoft.iso;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Helper class for read null terminated Strings.
 */
public class ZeroTermString {
    private static final int SIZE = 16;
    private byte[] buffer = new byte[SIZE];
    private int size;

    /**
     * Append the ByteBuffer until a zero is found
     * @return null if no zero found or the UTF-8 String if zero found
     */
    public String appendString(@NonNull ByteBuffer byteBuffer) {
        while (byteBuffer.hasRemaining()) {
            final byte b = byteBuffer.get();
            if (b == 0) {
                return new String(buffer, 0, size);
            }
            if (size == buffer.length) {
                buffer = Arrays.copyOf(buffer, buffer.length + SIZE);
            }
            buffer[size++] = b;
        }
        return null;
    }

}
