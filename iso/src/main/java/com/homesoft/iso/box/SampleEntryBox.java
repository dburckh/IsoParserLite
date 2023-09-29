package com.homesoft.iso.box;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class SampleEntryBox extends BaseContainerBox {
    @NonNull
    @Override
    public SampleEntry read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        streamReader.skip(6); // reserved
        return new SampleEntry(streamReader.getShort()); // dataReferenceIndex
    }
}
