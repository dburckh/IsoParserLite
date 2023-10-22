package com.homesoft.iso.box;

public interface IntArray {
    int size();
    int getInt(int index);

    int[] toInts();

    class Util {
        public static int[] toInts(IntArray intArray) {
            final int[] ints = new int[intArray.size()];
            for (int i=0;i<ints.length;i++) {
                ints[i] = intArray.getInt(i);
            }
            return ints;
        }
    }
}
