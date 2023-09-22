package com.homesoft.iso.parser;

import androidx.annotation.NonNull;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

public class ItemPropertyAssociation {
    private static final int[] EMPTY_ARRAY = new int[0];
    private final HashMap<Integer, Buffer> map = new HashMap<>();

    public int[] getAssociations(int id) {
        final Buffer associationBuffer = map.get(id);
        if (associationBuffer == null) {
            return EMPTY_ARRAY;
        }
        final int[] associations = new int[associationBuffer.capacity()];
        for (int i=0;i<associations.length;i++) {
            if (associationBuffer instanceof ByteBuffer) {
                associations[i] = ((ByteBuffer)associationBuffer).get(i) & 0x7f;
            } else if (associationBuffer instanceof ShortBuffer) {
                associations[i] = ((ShortBuffer)associationBuffer).get(i) & 0x7fff;
            }
        }
        return associations;
    }

    void put(int id, @NonNull Buffer associationBuffer) {
        map.put(id, associationBuffer);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{map=" +map +"}";
    }
}
