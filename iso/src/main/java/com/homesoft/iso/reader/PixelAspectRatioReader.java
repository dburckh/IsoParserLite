package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;

import java.io.IOException;

public class PixelAspectRatioReader implements TypedParser, BoxReader {
    @Override
    public PixelAspectRatio read(Box box, StreamReader streamReader) throws IOException {
        return new PixelAspectRatio(streamReader.getInt(), streamReader.getInt());
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_pasp;
    }
}
