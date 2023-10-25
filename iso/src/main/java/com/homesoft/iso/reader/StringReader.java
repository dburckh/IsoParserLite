package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class StringReader implements BoxReader {
    private final boolean fullBox;
    public StringReader(boolean fullBox) {
        this.fullBox = fullBox;
    }

    @Override
    public String read(Box box, StreamReader streamReader) throws IOException {
        if (fullBox) {
            streamReader.getInt(); //versionFlags
        }
        return streamReader.getString();
    }
}
