package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBoxParser;

import java.io.IOException;

/**
 *
 * <a href="https://github.com/nokiatech/heif/blob/master/srcs/common/hevcdecoderconfigrecord.cpp">...</a>
 */
public class HevcDecoderConfigParser implements TypedBoxParser {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public HevcDecoderConfig parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final byte[] bytes = streamReader.getBytes((int)box.getPayloadSize(isFullBox()));
        return new HevcDecoderConfig(bytes);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_hvcC;
    }
}
