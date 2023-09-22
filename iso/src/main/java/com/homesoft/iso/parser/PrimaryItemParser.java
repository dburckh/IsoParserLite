package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class PrimaryItemParser implements TypedBoxParser {
    @Override
    public boolean isFullBox() {
        return true;
    }

    /**
     *
     * @return the primary item ID
     */
    @Override
    public NumberType parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        int version = Box.getVersion(versionFlags);
        final Number number = version == 0 ?
                DataUtil.getUShort(streamReader) :
                DataUtil.getUInt(streamReader);
        return new NumberType(getType(), number);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_pitm;
    }
}
