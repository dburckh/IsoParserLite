package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class ByteArrayReader extends MaybeFullBox {
    public ByteArrayReader(boolean fullBox) {
        super(fullBox);
    }

    @Nullable
    @Override
    protected byte[] read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        return streamReader.getBytes((int)box.getPayloadSize(isFullBox()));
    }
}
