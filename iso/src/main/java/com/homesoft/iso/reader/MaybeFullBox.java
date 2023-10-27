package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public abstract class MaybeFullBox extends FullBoxReader {
    private final boolean fullBox;

    public MaybeFullBox(boolean fullBox) {
        this.fullBox = fullBox;
    }

    public boolean isFullBox() {
        return fullBox;
    }

    @Nullable
    @Override
    public Object read(Box box, StreamReader streamReader) throws IOException {
        if (fullBox) {
            return super.read(box, streamReader);
        } else {
            return read(box, streamReader, 0);
        }
    }
}
