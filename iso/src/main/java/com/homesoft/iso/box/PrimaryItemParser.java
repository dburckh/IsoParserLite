package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class PrimaryItemParser implements Box {
    @Override
    public boolean isFullBox() {
        return true;
    }

    /**
     *
     * @return the primary item ID
     */
    @Override
    public Number read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        int version = BoxHeader.getVersion(versionFlags);
        final Number number = version == 0 ?
                DataUtil.getUShort(streamReader) :
                streamReader.getInt();
        return number;
    }
}
