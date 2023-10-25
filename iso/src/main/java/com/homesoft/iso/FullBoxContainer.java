package com.homesoft.iso;

import androidx.annotation.Nullable;

import com.homesoft.iso.reader.BaseBoxContainer;

import java.io.IOException;

public class FullBoxContainer extends BaseBoxContainer implements BoxReader {
    @Nullable
    @Override
    public Object read(Box box, StreamReader streamReader) throws IOException {
        streamReader.skip(4);
        return null;
    }
}
