package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SingleItemTypeReferenceIntParser implements BoxParser {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public SingleItemTypeReference parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int fromId = streamReader.getInt();
        final int referenceCount = streamReader.getInt();
        final ByteBuffer byteBuffer = ByteBuffer.wrap(streamReader.getBytes(4 * referenceCount));
        return new SingleItemTypeReference(box.type, fromId, byteBuffer.asIntBuffer());
    }
}
