package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;

/**
 * Parser for the av1C box
 */
public class Av1DecoderConfigBox implements TypedBox {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public Object read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final byte[] bytes = streamReader.getBytes((int) boxHeader.getPayloadSize(isFullBox()));
        return new Av1DecoderConfig(bytes);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_av1C;
    }
}
