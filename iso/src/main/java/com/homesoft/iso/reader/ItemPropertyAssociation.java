package com.homesoft.iso.reader;

import com.homesoft.iso.Id;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class ItemPropertyAssociation implements Id {

    private final int id;
    private final Buffer buffer;

    public ItemPropertyAssociation(int id, Buffer buffer) {
        this.id = id;
        this.buffer = buffer;
    }

    @Override
    public int getId() {
        return id;
    }

    public int[] getAssociations() {
        final int[] associations = new int[buffer.capacity()];
        for (int i=0;i<associations.length;i++) {
            if (buffer instanceof ByteBuffer) {
                associations[i] = ((ByteBuffer)buffer).get(i) & 0x7f;
            } else if (buffer instanceof ShortBuffer) {
                associations[i] = ((ShortBuffer)buffer).get(i) & 0x7fff;
            }
        }
        return associations;
    }
    @Override
    public String toString() {
        return getClass().getSimpleName() +"{id=" +id + ", associations=" + Arrays.toString(getAssociations()) + "}";
    }
}
