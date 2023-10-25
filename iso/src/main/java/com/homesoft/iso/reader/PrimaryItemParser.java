package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class PrimaryItemParser extends FullBoxReader implements BoxReader {
    /**
     *
     * @return the primary item ID
     */
    @Override
    public Number read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        int version = Box.getVersion(versionFlags);
        final Number number = version == 0 ?
                StreamUtil.getUShort(streamReader) :
                streamReader.getInt();
        return number;
    }
}
