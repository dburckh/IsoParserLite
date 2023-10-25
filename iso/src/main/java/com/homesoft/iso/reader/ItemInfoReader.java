package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.BoxContainer;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Parser for Item Info (iinf)
 */
public class ItemInfoReader extends FullBoxReader implements BoxContainer {
    private static final ItemInfoEntryBox ITEM_INFO_ENTRY_PARSER = new ItemInfoEntryBox();
    @NonNull
    @Override
    public Integer read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = Box.getVersion(versionFlags);
        return version == 0 ? StreamUtil.getUShort(streamReader) : streamReader.getInt();
    }

    @Override
    public BoxReader getParser(Box box, Object dependency) {
        if (box.type == BoxTypes.TYPE_infe) {
            return ITEM_INFO_ENTRY_PARSER;
        }
        return null;
    }
}
