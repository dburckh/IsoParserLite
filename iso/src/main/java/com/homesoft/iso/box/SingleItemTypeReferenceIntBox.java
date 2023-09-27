package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SingleItemTypeReferenceIntBox implements Box {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public SingleItemTypeReference read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int fromId = streamReader.getInt();
        final int referenceCount = streamReader.getInt();
        final ByteBuffer byteBuffer = ByteBuffer.wrap(streamReader.getBytes(4 * referenceCount));
        return new SingleItemTypeReference(boxHeader.type, fromId, byteBuffer.asIntBuffer());
    }
}
