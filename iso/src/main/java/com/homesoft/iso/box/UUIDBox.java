package com.homesoft.iso.box;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.UUIDBoxHeader;

import java.io.IOException;

/**
 * Light weight wrapper around another box used to return the UUID of a box
 */
public class UUIDBox implements Box {
    private final Box delegate;

    public UUIDBox(@NonNull Box delegate) {
        this.delegate = delegate;
    }
    @Override
    public boolean isFullBox() {
        return delegate.isFullBox();
    }

    @Nullable
    @Override
    public UUIDResult read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        if (boxHeader instanceof UUIDBoxHeader) {
            return new UUIDResult(((UUIDBoxHeader) boxHeader).uuid,
                    delegate.read(boxHeader, streamReader, versionFlags));
        } else {
            throw new IllegalArgumentException("Expected UUIDBoxHeader");
        }
    }
}
