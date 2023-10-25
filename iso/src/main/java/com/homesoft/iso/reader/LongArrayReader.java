package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class LongArrayReader extends FullBoxReader {
    @Nullable
    @Override
    public LongArray read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int count = streamReader.getInt();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(count * 8);
        streamReader.read(byteBuffer);
        return new LongBufferArray(byteBuffer);
    }
}
