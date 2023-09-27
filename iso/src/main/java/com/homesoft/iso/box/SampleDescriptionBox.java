package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ResultResolver;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class SampleDescriptionBox extends BaseContainerBox {
    private final ResultResolver resultResolver;

    public SampleDescriptionBox(ResultResolver resultResolver) {
        super(true, false);
        this.resultResolver = resultResolver;
    }

    @Nullable
    @Override
    public Integer read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        return streamReader.getInt(); // entryCount
    }

    @Nullable
    @Override
    public Box getParser(int type) {
        // Try get a specific parser first
        Box box = super.getParser(type);
        if (box == null) {
            // If we fail, try to get one by handler
            Integer handler = (Integer) resultResolver.getResult(BoxTypes.TYPE_hdlr);
            box = super.getParser(handler);
        }
        return box;
    }
}
