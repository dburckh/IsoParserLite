package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;

/**
 *
 * <a href="https://github.com/nokiatech/heif/blob/master/srcs/common/hevcdecoderconfigrecord.cpp">...</a>
 */
public class HevcDecoderConfigBox implements TypedBox {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public HevcDecoderConfig read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final byte[] bytes = streamReader.getBytes((int) boxHeader.getPayloadSize(isFullBox()));
        return new HevcDecoderConfig(bytes);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_hvcC;
    }
}
