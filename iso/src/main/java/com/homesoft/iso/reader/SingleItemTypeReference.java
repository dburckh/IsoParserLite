package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.Type;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class SingleItemTypeReference implements Type {
    public static final int TYPE_dimg = 0x64696D67; // derived image
    public static final int TYPE_thmb = 0x74686D62; // thumbnail
    public static final int TYPE_cdsc = 0x63647363; // metadata
    public static final int TYPE_auxl = 0x6175786C; // Auxiliary Image

    public final int type;
    public final int fromId;
    private final Buffer toIdBuffer;

    private static int[] toInts(ShortBuffer shortBuffer) {
        final int[] toInts = new int[shortBuffer.capacity()];
        for (int i=0;i < toInts.length;i++) {
            toInts[i] = shortBuffer.get(i) & StreamUtil.USHORT_MASK;
        }
        return toInts;
    }

    private static int[] toInts(IntBuffer intBuffer) {
        final int[] toInts = new int[intBuffer.capacity()];
        for (int i=0;i < toInts.length;i++) {
            toInts[i] = intBuffer.get(i);
        }
        return toInts;
    }

    public SingleItemTypeReference(int type, int fromId, @NonNull Buffer toIdBuffer) {
        this.type = type;
        this.fromId = fromId;
        this.toIdBuffer = toIdBuffer;
    }

    @Override
    public int getType() {
        return type;
    }
    public int getFromId() {
        return fromId;
    }

    /**
     * Get the reference toIds in order
     */
    public int[] getToIds() {
        int[] ids;
        if (toIdBuffer instanceof ShortBuffer) {
            ids = toInts((ShortBuffer) toIdBuffer);
        } else if (toIdBuffer instanceof IntBuffer) {
            ids = toInts((IntBuffer) toIdBuffer);
        } else {
            return new int[0];
        }
        Arrays.sort(ids);
        return ids;
    }

    @Override
    public String toString() {
        return "{type=" + Box.typeToString(type) + ", fromId=" + fromId +", toIds=" +
                Arrays.toString(getToIds()) + "}";
    }
}
