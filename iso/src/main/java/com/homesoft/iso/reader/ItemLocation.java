package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.Id;
import com.homesoft.iso.Type;

import java.util.Arrays;

public class ItemLocation implements Id, Type {
    private static final Extent[] EMPTY_EXTENTS = new Extent[0];
    public final int id;

    private final short constructionMethod;
    public final short dataReferenceIndex;
    public final long baseOffset;

    private Extent[] extents = EMPTY_EXTENTS;

    public int getExtentCount() {
        return extents.length;
    }

    public long getExtentOffset(int index) {
        return baseOffset + extents[index].offset;
    }
    public long getExtentLength(int index) {
        return extents[index].size;
    }


    ItemLocation(int id, short constructionMethod, short dataReferenceIndex, long baseOffset) {
        this.id = id;
        this.constructionMethod = constructionMethod;
        this.dataReferenceIndex = dataReferenceIndex;
        this.baseOffset = baseOffset;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getType() {
        return BoxTypes.TYPE_iloc;
    }

    public void setExtents(@NonNull Extent[] extents) {
        this.extents = extents;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{id=");
        sb.append(id);
        final int c = getExtentCount();
        if (c > 0) {
            sb.append(", extents=");
            sb.append(Arrays.toString(extents));
        }
        sb.append('}');
        return sb.toString();
    }
}
