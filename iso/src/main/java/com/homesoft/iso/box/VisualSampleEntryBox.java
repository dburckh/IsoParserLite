package com.homesoft.iso.box;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class VisualSampleEntryBox extends SampleEntryBox {
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
    public VisualSampleEntry read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final SampleEntry sampleEntry = super.read(boxHeader, streamReader, versionFlags);

        streamReader.skip(SKIP1);
        final VisualSampleEntry visualSampleEntry = new VisualSampleEntry(sampleEntry,
                streamReader.getShort(), streamReader.getShort());
        streamReader.skip(SKIP2);
        return visualSampleEntry;
    }
}
