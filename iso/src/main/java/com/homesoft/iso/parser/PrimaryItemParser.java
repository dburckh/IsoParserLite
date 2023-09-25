package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class PrimaryItemParser implements BoxParser {
    @Override
    public boolean isFullBox() {
        return true;
    }

    /**
     *
     * @return the primary item ID
     */
    @Override
    public Number parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        int version = Box.getVersion(versionFlags);
        final Number number = version == 0 ?
                DataUtil.getUShort(streamReader) :
                streamReader.getInt();
        return number;
    }
}
