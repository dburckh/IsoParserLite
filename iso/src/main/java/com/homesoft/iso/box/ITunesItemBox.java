package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.ContainerBox;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * General Rules:
 * Numbers and classes where locale doesn't apply are returned as the actually class.
 * String and binary data are returned wrapped in a {@link Data} object to preserve the locale.
 */
public class ITunesItemBox implements ContainerBox {

    private static final int TYPE_data = 0x64617461;

    private BoxHeader itemBoxHeader;
    private final Box dataBox = new Box() {
        @Override
        public boolean isFullBox() {
            return false;
        }

        @Override
        public Object read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
            int dataType = streamReader.getInt();
            final int locale = streamReader.getInt();
            final int dataSize = (int)boxHeader.getPayloadSize(false) - 8;
            switch (itemBoxHeader.type) {
                case ItemListBox.TYPE_trkn:
                case ItemListBox.TYPE_disk:
                    streamReader.getShort(); //Unknown
                    return new SetIndex(DataUtil.getUShort(streamReader), DataUtil.getUShort(streamReader));
                case ItemListBox.TYPE_gnre:
                    dataType = Data.BE_UNSIGNED;
                    break;
            }
            final Object data;
            switch (dataType) {
                case Data.UTF_8:
                    data = new String(streamReader.getBytes(dataSize));
                    break;
                case Data.BE_UNSIGNED:
                    return DataUtil.getNumber(streamReader, dataSize, false);
                case Data.BE_SIGNED:
                    return DataUtil.getNumber(streamReader, dataSize, true);
                case Data.JPEG:
                case Data.PNG:
                case Data.BMP:
                    data = new Extent(streamReader.position(), dataSize);
                    break;
                default:
                    data = streamReader.getBytes(dataSize);
            }
            return new Data(boxHeader.type, dataType, locale, data);
        }
    };

    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public Object read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        itemBoxHeader = boxHeader;
        return null;
    }

    @Nullable
    @Override
    public Box getBox(int type) {
        // There is also itif and name
        if (type == TYPE_data) {
            return dataBox;
        }
        return null;
    }
}
