package com.homesoft.iso.reader;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class LongBufferArray implements LongArray {
    private final LongBuffer longBuffer;

    public LongBufferArray(ByteBuffer byteBuffer) {
        this.longBuffer = ((ByteBuffer)byteBuffer.clear()).asLongBuffer();
    }

    @Override
    public int size() {
        return longBuffer.capacity();
    }

    @Override
    public long getLong(int index) {
        return longBuffer.get(index);
    }

    @Override
    public long[] getLongs() {
        long[] longs = new long[longBuffer.capacity()];
        longBuffer.duplicate().get(longs);
        return longs;
    }
}
