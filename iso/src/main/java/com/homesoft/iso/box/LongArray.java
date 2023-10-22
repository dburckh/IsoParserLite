package com.homesoft.iso.box;

public interface LongArray {
    int size();
    long getLong(int index);
    long[] getLongs();

    class Util {
        public static long[] getLongs(LongArray longArray) {
            final long[] longs = new long[longArray.size()];
            for (int i=0;i<longs.length;i++) {
                longs[i] = longArray.getLong(i);
            }
            return longs;
        }
    }
}
