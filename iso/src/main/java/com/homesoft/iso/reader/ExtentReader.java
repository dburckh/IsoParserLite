package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class ExtentReader implements BoxReader {
    @Override
    public Extent read(Box box, StreamReader streamReader) throws IOException {
        final long start = streamReader.position() - box.getHeaderSize(false);
        return new Extent(start, start + box.getSize());
    }
}
