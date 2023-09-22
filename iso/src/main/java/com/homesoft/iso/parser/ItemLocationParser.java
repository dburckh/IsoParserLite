package com.homesoft.iso.parser;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ItemLocationParser implements TypedBoxParser {

    @NonNull
    @Override
    public ItemLocation[] parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final short s = streamReader.getShort();
        final int version = Box.getVersion(versionFlags);
        final int offsetSize =     (s & 0xf000) >> 12;
        final int lengthSize =     (s & 0x0f00) >> 8;
        final int baseOffsetSize = (s & 0x00f0) >> 4;
        final short itemCount = streamReader.getShort();
        final ItemLocation[] itemLocations = new ItemLocation[itemCount];
        final int itemIdSize = version < 2 ? 2 : 4;
        final int constructionMethodSize = (version == 1 || version == 2) ? 2 : 0;
        for (int i=0; i<itemCount;i++) {
            final ByteBuffer byteBuffer = streamReader.getSharedBuffer(itemIdSize + constructionMethodSize + 2 + baseOffsetSize + 2);
            final ItemLocation itemLocation = new ItemLocation(
                    DataUtil.getInt(byteBuffer, itemIdSize),
                    constructionMethodSize == 2 ? byteBuffer.getShort() : 0,
                    byteBuffer.getShort(),
                    getUInt(byteBuffer, baseOffsetSize)
            );
            final short extentCount = byteBuffer.getShort();
            if (extentCount > 0) {
                final Extent[] extents = new Extent[extentCount];
                for (int j=0; j<extentCount; j++) {
                    final ByteBuffer extentBuffer = streamReader.getSharedBuffer(offsetSize + lengthSize);
                    extents[j] = new Extent(getUInt(extentBuffer, offsetSize), getUInt(extentBuffer, lengthSize));
                }
                itemLocation.setExtents(extents);
            }
            itemLocations[i] = itemLocation;
        }
        return itemLocations;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_iloc;
    }

    @Override
    public boolean isFullBox() {
        return true;
    }

    private static long getUInt(ByteBuffer byteBuffer, int size) {
        switch (size) {
            case 2:
                return DataUtil.getUShort(byteBuffer);
            case 4:
                return DataUtil.getUInt(byteBuffer);
            case 0:
                return 0;
            default:
                throw new IllegalArgumentException("Unexpected size: " + size);
        }
    }
}
