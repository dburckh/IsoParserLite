package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBoxParser;

import java.io.IOException;

/**
 * Parser for the av1C box
 */
public class Av1DecoderConfigParser implements TypedBoxParser {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public Object parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final byte[] bytes = streamReader.getBytes((int)box.getPayloadSize(isFullBox()));
        return new Av1DecoderConfig(bytes);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_av1C;
    }
}
