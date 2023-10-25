package com.homesoft.iso.reader;

import com.homesoft.iso.StreamUtil;

public class SampleEntry {
    private final short dataReferenceIndex;

    protected SampleEntry(SampleEntry sampleEntry) {
        this.dataReferenceIndex = sampleEntry.dataReferenceIndex;
    }
    public SampleEntry(short dataReferenceIndex) {
        this.dataReferenceIndex = dataReferenceIndex;
    }

    public int getDataReferenceIndex() {
        return dataReferenceIndex & StreamUtil.USHORT_MASK;
    }

    public String toStringPrefix() {
        return getClass().getSimpleName() + "{dataReferenceIndex=" + dataReferenceIndex;
    }

    @Override
    public String toString() {
        return toStringPrefix() + "}";
    }
}
