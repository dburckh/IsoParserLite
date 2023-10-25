package com.homesoft.iso.reader.cr3;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;
import com.homesoft.iso.reader.VisualSampleEntry;
import com.homesoft.iso.reader.VisualSampleEntryReader;

import java.io.IOException;

public class CRawVisualSampleEntryReader extends VisualSampleEntryReader implements TypedParser {
    public static final int TYPE_CRAW = 0x43524157;
    @NonNull
    @Override
    public CRawVisualSampleEntry read(Box box, StreamReader streamReader) throws IOException {
        VisualSampleEntry visualSampleEntry = super.read(box, streamReader);
        return new CRawVisualSampleEntry(visualSampleEntry, streamReader.getShort());
    }

    @Override
    public int getType() {
        return TYPE_CRAW;
    }
}
