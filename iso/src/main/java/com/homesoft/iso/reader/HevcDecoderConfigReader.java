package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;

import java.io.IOException;

/**
 *
 * <a href="https://github.com/nokiatech/heif/blob/master/srcs/common/hevcdecoderconfigrecord.cpp">...</a>
 */
public class HevcDecoderConfigReader implements TypedParser, BoxReader {
    @Override
    public HevcDecoderConfig read(Box box, StreamReader streamReader) throws IOException {
        final byte[] bytes = streamReader.getBytes((int) box.getPayloadSize(false));
        return new HevcDecoderConfig(bytes);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_hvcC;
    }
}
