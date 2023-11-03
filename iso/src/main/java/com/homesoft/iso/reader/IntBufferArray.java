package com.homesoft.iso.reader;

import com.homesoft.iso.StreamUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class IntBufferArray implements IntArray, LongArray {
    private final IntBuffer intBuffer;

    public IntBufferArray(ByteBuffer byteBuffer) {
        intBuffer = ((ByteBuffer)byteBuffer.clear()).asIntBuffer();
    }

    @Override
    public int size() {
        return intBuffer.capacity();
    }

    @Override
    public int getInt(int index) {
        return intBuffer.get(index);
    }

    @Override
    public int[] toInts() {
        final int[] ints = new int[intBuffer.capacity()];
        intBuffer.duplicate().get(ints);
        return ints;
    }

    @Override
    public long getLong(int index) {
        return intBuffer.get(index) & StreamUtil.UINT_MASK;
    }

    @Override
    public long[] getLongs() {
        return LongArray.Util.getLongs(this);
    }
}
