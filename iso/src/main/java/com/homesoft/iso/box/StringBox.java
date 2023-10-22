package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class StringBox extends BaseBox {
    public StringBox(boolean fullBox) {
        super(fullBox);
    }

    @Override
    public String read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        return streamReader.getString();
    }
}
