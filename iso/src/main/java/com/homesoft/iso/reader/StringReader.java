package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class StringReader extends MaybeFullBox {
    public StringReader(boolean fullBox) {
        super(fullBox);
    }

    @Nullable
    @Override
    protected String read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        return streamReader.getString();
    }
}
