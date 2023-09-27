package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class StringBox implements Box {
    private final boolean fullBox;
    public StringBox(boolean fullBox) {
        this.fullBox = fullBox;
    }
    @Override
    public boolean isFullBox() {
        return fullBox;
    }

    @Override
    public StringType read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        return new StringType(boxHeader.type, streamReader.getString());
    }
}
