package com.homesoft.iso.reader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.BoxReader;
import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Light weight wrapper around another box used to return the UUID of a box
 */
public class UUIDBox implements BoxReader {
    private final BoxReader delegate;

    public UUIDBox(@NonNull BoxReader delegate) {
        this.delegate = delegate;
    }

    @Nullable
    @Override
    public UUIDResult read(Box box, StreamReader streamReader) throws IOException {
        if (box instanceof com.homesoft.iso.UUIDBox) {
            return new UUIDResult(((com.homesoft.iso.UUIDBox) box).uuid,
                    delegate.read(box, streamReader));
        } else {
            throw new IllegalArgumentException("Expected UUIDBoxHeader");
        }
    }
}
