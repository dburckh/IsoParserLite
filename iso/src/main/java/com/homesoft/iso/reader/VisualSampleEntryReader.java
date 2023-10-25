package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class VisualSampleEntryReader extends SampleEntryReader {
    private static final int SKIP1 =2 + // pre_defined = 0
                                    2 + // reserved
                                    4 * 3; //pre_defined = 0
    private static final int SKIP2 =4 + // horizresolution - usually 72 dpi
                                    4 + // vertresolution - usually 72 dpi
                                    4 + // reserved
                                    2 + // frame_count = 1
                                    32+ // compressorname
                                    2 + // depth = 0x0018
                                    2;  // pre_defined = -1
    @NonNull
    @Override
    public VisualSampleEntry read(Box box, StreamReader streamReader) throws IOException {
        final SampleEntry sampleEntry = super.read(box, streamReader);

        streamReader.skip(SKIP1);
        final VisualSampleEntry visualSampleEntry = new VisualSampleEntry(sampleEntry,
                streamReader.getShort(), streamReader.getShort());
        streamReader.skip(SKIP2);
        return visualSampleEntry;
    }
}
