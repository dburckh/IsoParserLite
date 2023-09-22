package com.homesoft.iso.parser;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ItemInfoEntryParser implements BoxParser {

    @NonNull
    @Override
    public ItemInfoEntry parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        // https://github.com/nokiatech/heif/blob/master/srcs/common/iteminfobox.cpp
        final int version = Box.getVersion(versionFlags);

        final long payloadSize = box.getPayloadSize(isFullBox());
        final long end = streamReader.position() + payloadSize;

        final ByteBuffer byteBuffer = DataUtil.getByteBuffer(payloadSize, streamReader);

        final int id;
        final short protectionIndex;
        int itemType = ItemInfoEntry.ITEM_TYPE_NA;
        String name;
        String contentType = null;
        String contentEncoding = null;

        switch (version) {
            case 0:
            case 1:
                id = byteBuffer.getShort();
                protectionIndex = byteBuffer.getShort();
                name = streamReader.getString();
                contentType = streamReader.getString();
                if (streamReader.position() < end) {
                    contentEncoding = streamReader.getString();
                }

                break;
            default: // >=2
                if (version == 2) {
                    id = byteBuffer.getShort();
                } else if (version == 3) {
                    id = byteBuffer.getInt();
                } else {
                    id = 0;
                }
                protectionIndex = byteBuffer.getShort();
                itemType = byteBuffer.getInt();
                name = streamReader.getString();
                if (itemType == ItemInfoEntry.ITEM_TYPE_mime) {
                    contentType = streamReader.getString();
                    if (byteBuffer.position() < end) {
                        contentEncoding = streamReader.getString();
                    }
                }
                break;

        }
        //Skip the ItemInfoExtension if present
        return new ItemInfoEntry(id, protectionIndex, name, contentType, contentEncoding, itemType);
    }

    @Override
    public boolean isFullBox() {
        return true;
    }
}
