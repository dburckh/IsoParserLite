package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SingleItemTypeReferenceParser implements BoxParser {

    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public SingleItemTypeReference parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int fromId = DataUtil.getUShort(streamReader);
        final int referenceCount = DataUtil.getUShort(streamReader);
        final ByteBuffer byteBuffer = ByteBuffer.wrap(streamReader.getBytes(2 * referenceCount));
        return new SingleItemTypeReference(box.type, fromId, byteBuffer.asShortBuffer());
    }
}
