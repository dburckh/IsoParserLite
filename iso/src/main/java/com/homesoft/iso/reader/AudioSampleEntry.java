package com.homesoft.iso.reader;

import com.homesoft.iso.StreamUtil;

public class AudioSampleEntry extends SampleEntry {
    private final short channelCount;
    private final short sampleSize;

    private final int sampleRate;
    public AudioSampleEntry(SampleEntry sampleEntry, short channelCount, short sampleSize,
                            int sampleRate) {
        super(sampleEntry);
        this.channelCount = channelCount;
        this.sampleSize = sampleSize;
        this.sampleRate = sampleRate;
    }

    public int getChannelCount() {
        return StreamUtil.getUShort(channelCount);
    }

    public int getSampleSize() {
        return StreamUtil.getUShort(sampleSize);
    }

    public int getSampleRate() {
        return (sampleRate >> 16) & StreamUtil.USHORT_MASK ;
    }

    @Override
    public String toString() {
        return toStringPrefix() + ", channels=" + getChannelCount() + ", size=" + getSampleSize() + ", rate=" + getSampleRate() +"}";
    }
}
