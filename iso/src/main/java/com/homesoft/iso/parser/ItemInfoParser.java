package com.homesoft.iso.parser;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.ContainerParser;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Parser for Item Info (iinf)
 */
public class ItemInfoParser implements ContainerParser {
    private static final ItemInfoEntryParser ITEM_INFO_ENTRY_PARSER = new ItemInfoEntryParser();
    @NonNull
    @Override
    public Integer parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = Box.getVersion(versionFlags);
        return version == 0 ? DataUtil.getUShort(streamReader) : streamReader.getInt();
    }

    @Override
    public BoxParser getParser(int type) {
        return ITEM_INFO_ENTRY_PARSER;
    }

    @Override
    public boolean isFullBox() {
        return true;
    }
}
