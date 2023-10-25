package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.TypedParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class CompactSampleSizeReader extends FullBoxReader implements TypedParser{
    @Nullable
    @Override
    public Object read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int size = streamReader.getInt() & 0xff;
        final int count = streamReader.getInt();

        IntArray intArray;
        switch (size) {
            case 4: {
                intArray = new NibbleIntArray(streamReader.getBytes((count + 1) / 2), count);
                break;
            }
            case 8:
                intArray = new ByteIntArray(streamReader.getBytes(count));
                break;
            case 16: {
                ByteBuffer byteBuffer = ByteBuffer.allocate(count * 2);
                streamReader.read(byteBuffer);
                intArray = new ShortIntArray(byteBuffer);
                break;
            }
            default:
                throw new IllegalArgumentException("Size: " + size + " must = [4|8|16]");
        }
        return intArray;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_stz2;
    }

    private static class NibbleIntArray implements IntArray {
        private final int size;
        private final byte[] bytes;

        public NibbleIntArray(byte[] bytes, int size) {
            this.bytes = bytes;
            this.size = size;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int getInt(int index) {
            if (index < 0 || index >= size) {
                throw new ArrayIndexOutOfBoundsException();
            }
            int i = bytes[index / 2];
            if ((index & 1) == 0) {
                i >>= 4;
            }
            return i & 0xf;
        }

        @Override
        public int[] toInts() {
            return Util.toInts(this);
        }
    }

    private static class ByteIntArray implements IntArray {
        private final byte[] bytes;

        public ByteIntArray(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public int size() {
            return bytes.length;
        }

        @Override
        public int getInt(int index) {
            return bytes[index] & 0xff;
        }

        @Override
        public int[] toInts() {
            return Util.toInts(this);
        }

    }

    private static class ShortIntArray implements IntArray {
        private final ShortBuffer shortBuffer;

        public ShortIntArray(ByteBuffer byteBuffer) {
            this.shortBuffer = byteBuffer.asShortBuffer();
        }

        @Override
        public int size() {
            return shortBuffer.capacity();
        }

        @Override
        public int getInt(int index) {
            return shortBuffer.get(index) & StreamUtil.USHORT_MASK;
        }

        @Override
        public int[] toInts() {
            return Util.toInts(this);
        }
    }
}
