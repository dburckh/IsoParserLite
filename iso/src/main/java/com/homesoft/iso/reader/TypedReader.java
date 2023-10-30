package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedResult;

import java.io.IOException;

/**
 * Wraps a {@link BoxReader}'s result in a {@link TypedResult}
 */
public class TypedReader implements BoxReader {
    private final BoxReader delegate;

    public TypedReader(BoxReader delegate) {
        this.delegate = delegate;
    }

    @Nullable
    @Override
    public TypedResult read(Box box, StreamReader streamReader) throws IOException {
        return new TypedResult(box.type, delegate.read(box, streamReader));
    }
}
