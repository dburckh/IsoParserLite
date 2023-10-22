package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SampleSizeBox implements TypedBox {
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Nullable
    @Override
    public IntArray read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int value = streamReader.getInt(); // sample_size
        final int count = streamReader.getInt(); // sample_count
        if (value != 0) {
            return new FixedIntArray(count, value);
        }
        final ByteBuffer byteBuffer = ByteBuffer.allocate(count * 4);
        streamReader.read(byteBuffer);
        return new IntBufferArray(byteBuffer);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_stsz;
    }

    private static class FixedIntArray implements IntArray {
        private final int size;
        private final int value;

        public FixedIntArray(int size, int value) {
            this.size = size;
            this.value = value;
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
            return value;
        }

        @Override
        public int[] toInts() {
            int[] ints = new int[size];
            Arrays.fill(ints, value);
            return ints;
        }
    }
}
