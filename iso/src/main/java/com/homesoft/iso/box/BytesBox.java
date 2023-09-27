package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Generic parser that returns a {@link BytesType} object
 */
public class BytesBox implements Box {

    private final boolean fullBox;

    public BytesBox(boolean fullBox) {
        this.fullBox = fullBox;
    }

    @Override
    public boolean isFullBox() {
        return fullBox;
    }

    @Override
    public BytesType read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final long payloadSize = boxHeader.getPayloadSize(isFullBox());
        return new BytesType(boxHeader.type, streamReader.getBytes((int)payloadSize));
    }

}
