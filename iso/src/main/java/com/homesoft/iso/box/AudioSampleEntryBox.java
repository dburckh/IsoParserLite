package com.homesoft.iso.box;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class AudioSampleEntryBox extends SampleEntryBox {
    @NonNull
    @Override
    public AudioSampleEntry read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final SampleEntry sampleEntry = super.read(boxHeader, streamReader, versionFlags);
        streamReader.skip(4 * 2); //reserved
        final short channelCount = streamReader.getShort();
        final short sampleSize = streamReader.getShort();
        streamReader.skip(2 + 2);
        final int sampleRate = streamReader.getInt();
        return new AudioSampleEntry(sampleEntry, channelCount, sampleSize, sampleRate);
    }
}
