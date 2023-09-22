package com.homesoft.iso.parser;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.ContainerParser;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.IsoParser;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Parser for Item Info (iinf)
 */
public class ItemInfoParser implements ContainerParser {
    private static final ItemInfoEntryParser ITEM_INFO_ENTRY_PARSER = new ItemInfoEntryParser();
    @NonNull
    @Override
    public ItemInfoEntry[] parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = Box.getVersion(versionFlags);
        final int entryCount = version == 0 ? DataUtil.getUShort(streamReader) : streamReader.getInt();
        final Object[] objects = IsoParser.parse(this, streamReader,
                DataUtil.getBoxEnd(box, isFullBox(), streamReader));

        return DataUtil.copyArray(objects, ItemInfoEntry.class);
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
