package com.homesoft.iso;

import androidx.annotation.Nullable;

import java.io.IOException;

public abstract class FullBoxReader implements BoxReader {
    @Nullable
    @Override
    public Object read(Box box, StreamReader streamReader) throws IOException {
        return read(box, streamReader, streamReader.getInt());
    }

    /**
     * Parse the Box
     * @param box contains generic box metadata
     * @return the contents of the box.  See individual implementations for specifics.
     */
    @Nullable
    protected abstract Object read(Box box, StreamReader streamReader, int versionFlags) throws IOException;
}
