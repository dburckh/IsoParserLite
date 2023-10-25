package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class AudioSampleEntryReader extends SampleEntryReader {
    @NonNull
    @Override
    public AudioSampleEntry read(Box box, StreamReader streamReader) throws IOException {
        final SampleEntry sampleEntry = super.read(box, streamReader);
        streamReader.skip(4 * 2); //reserved
        final short channelCount = streamReader.getShort();
        final short sampleSize = streamReader.getShort();
        streamReader.skip(2 + 2);
        final int sampleRate = streamReader.getInt();
        return new AudioSampleEntry(sampleEntry, channelCount, sampleSize, sampleRate);
    }
}
