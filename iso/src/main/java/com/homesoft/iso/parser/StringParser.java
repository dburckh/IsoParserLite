package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class StringParser implements BoxParser {
    private final boolean fullBox;
    public StringParser(boolean fullBox) {
        this.fullBox = fullBox;
    }
    @Override
    public boolean isFullBox() {
        return fullBox;
    }

    @Override
    public StringType parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        return new StringType(box.type, streamReader.getString());
    }
}
