package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class ExtentBox implements Box {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public Extent read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final long start = streamReader.position() - boxHeader.getHeaderSize(isFullBox());
        return new Extent(start, start + boxHeader.getSize());
    }
}
