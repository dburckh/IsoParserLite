package com.homesoft.iso.reader.cr3;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.UUIDBox;
import com.homesoft.iso.reader.BaseBoxContainer;
import com.homesoft.iso.reader.UUIDResult;

import java.io.IOException;

public class PreviewContainerReader extends BaseBoxContainer implements BoxReader {
    @Nullable
    @Override
    public UUIDResult read(Box box, StreamReader streamReader) throws IOException {
        streamReader.skip(4);
        if (box instanceof UUIDBox) {
            return new UUIDResult(((UUIDBox) box).uuid, streamReader.getInt());
        }
        return null;
    }
}
