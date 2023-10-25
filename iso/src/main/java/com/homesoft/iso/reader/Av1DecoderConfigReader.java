package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;

import java.io.IOException;

/**
 * Parser for the av1C box
 */
public class Av1DecoderConfigReader implements TypedParser, BoxReader {

    @Override
    public Object read(Box box, StreamReader streamReader) throws IOException {
        final byte[] bytes = streamReader.getBytes((int) box.getPayloadSize(false));
        return new Av1DecoderConfig(bytes);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_av1C;
    }
}
