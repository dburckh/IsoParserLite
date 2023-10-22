package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class LongArrayBox extends BaseBox {
    public LongArrayBox(boolean fullBox) {
        super(fullBox);
    }

    @Nullable
    @Override
    public LongArray read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int count = streamReader.getInt();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(count * 8);
        streamReader.read(byteBuffer);
        return new BufferLongArray(byteBuffer);
    }
}
