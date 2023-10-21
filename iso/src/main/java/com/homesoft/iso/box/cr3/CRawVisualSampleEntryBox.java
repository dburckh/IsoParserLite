package com.homesoft.iso.box.cr3;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;
import com.homesoft.iso.box.VisualSampleEntry;
import com.homesoft.iso.box.VisualSampleEntryBox;

import java.io.IOException;

public class CRawVisualSampleEntryBox extends VisualSampleEntryBox implements TypedBox {
    public static final int TYPE_CRAW = 0x43524157;
    @NonNull
    @Override
    public CRawVisualSampleEntry read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        VisualSampleEntry visualSampleEntry = super.read(boxHeader, streamReader, versionFlags);
        return new CRawVisualSampleEntry(visualSampleEntry, streamReader.getShort());
    }

    @Override
    public int getType() {
        return TYPE_CRAW;
    }
}
