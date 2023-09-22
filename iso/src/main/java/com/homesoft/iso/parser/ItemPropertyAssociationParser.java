package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ItemPropertyAssociationParser implements TypedBoxParser {
    @Override
    public ItemPropertyAssociation parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int entryCount = streamReader.getInt();
        final int version = Box.getVersion(versionFlags);
        final int flags = Box.getFlags(versionFlags);
        boolean propertyIndexIsShort = (flags & 0x1) != 0;
        final ItemPropertyAssociation itemPropertyAssociation = new ItemPropertyAssociation();
        for (int i=0;i<entryCount;i++) {
            final int id;
            if (version < 1) {
                id = DataUtil.getUShort(streamReader);
            } else {
                id = streamReader.getInt();
            }
            int associationSize = DataUtil.getUByte(streamReader);
            if (propertyIndexIsShort) {
                associationSize *=2;
            }
            final byte[] buffer = streamReader.getBytes(associationSize);
            final ByteBuffer workBuffer = ByteBuffer.wrap(buffer);
            final Buffer associationBuffer = propertyIndexIsShort ? workBuffer.asShortBuffer() : workBuffer;
            itemPropertyAssociation.put(id, associationBuffer);
        }
        return itemPropertyAssociation;
    }

    @Override
    public boolean isFullBox() {
        return true;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_ipma;
    }
}
