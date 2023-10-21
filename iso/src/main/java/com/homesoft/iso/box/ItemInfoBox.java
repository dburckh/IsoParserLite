package com.homesoft.iso.box;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ContainerBox;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Parser for Item Info (iinf)
 */
public class ItemInfoBox implements ContainerBox {
    private static final ItemInfoEntryBox ITEM_INFO_ENTRY_PARSER = new ItemInfoEntryBox();
    @NonNull
    @Override
    public Integer read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = BoxHeader.getVersion(versionFlags);
        return version == 0 ? StreamUtil.getUShort(streamReader) : streamReader.getInt();
    }

    @Override
    public Box getBox(BoxHeader boxHeader) {
        if (boxHeader.type == BoxTypes.TYPE_infe) {
            return ITEM_INFO_ENTRY_PARSER;
        }
        return null;
    }

    @Override
    public boolean isFullBox() {
        return true;
    }
}
