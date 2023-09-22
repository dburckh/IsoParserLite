package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Generic parser that returns a {@link BytesType} object
 */
public class BytesParser implements BoxParser {

    private final boolean fullBox;

    public BytesParser(boolean fullBox) {
        this.fullBox = fullBox;
    }

    @Override
    public boolean isFullBox() {
        return fullBox;
    }

    @Override
    public BytesType parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final long payloadSize = box.getPayloadSize(isFullBox());
        return new BytesType(box.type, streamReader.getBytes((int)payloadSize));
    }

}
