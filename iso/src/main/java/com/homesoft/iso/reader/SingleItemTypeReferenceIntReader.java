package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SingleItemTypeReferenceIntReader implements BoxReader {
    @Override
    public SingleItemTypeReference read(Box box, StreamReader streamReader) throws IOException {
        final int fromId = streamReader.getInt();
        final int referenceCount = streamReader.getInt();
        final ByteBuffer byteBuffer = ByteBuffer.wrap(streamReader.getBytes(4 * referenceCount));
        return new SingleItemTypeReference(box.type, fromId, byteBuffer.asIntBuffer());
    }
}
