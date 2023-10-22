package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class IntArrayBox extends BaseBox {
    public IntArrayBox(boolean fullBox) {
        super(fullBox);
    }

    @Nullable
    @Override
    public IntBufferArray read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int count = streamReader.getInt();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(count * 4);
        streamReader.read(byteBuffer);
        return new IntBufferArray(byteBuffer);
    }
}
