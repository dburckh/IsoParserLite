package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SingleItemTypeReferenceReader implements BoxReader {
    @Override
    public SingleItemTypeReference read(Box box, StreamReader streamReader) throws IOException {
        final int fromId = StreamUtil.getUShort(streamReader);
        final int referenceCount = StreamUtil.getUShort(streamReader);
        final ByteBuffer byteBuffer = ByteBuffer.wrap(streamReader.getBytes(2 * referenceCount));
        return new SingleItemTypeReference(box.type, fromId, byteBuffer.asShortBuffer());
    }
}
