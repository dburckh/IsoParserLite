package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class ExtentBoxParser implements BoxParser  {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public Extent parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final long start = streamReader.position() - box.getHeaderSize(isFullBox());
        return new Extent(start, start + box.getSize());
    }
}
