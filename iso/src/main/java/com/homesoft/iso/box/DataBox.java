package com.homesoft.iso.box;


import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;

public class DataBox implements TypedBox {
    /**
     * Used as an override
     */
    private final int dataType;

    public DataBox() {
        this(BoxTypes.TYPE_NA);
    }

    /**
     * Force a dataType
     * This is necessary because some iTunes types are "well known" and have 0 for dataType
     * @param dataType from {@link Data}
     */
    public DataBox(int dataType) {
        this.dataType = dataType;
    }

    @Override
    public boolean isFullBox() {
        return false;
    }

    @Nullable
    @Override
    public Object read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int dataType;
        if (this.dataType == BoxTypes.TYPE_NA) {
            dataType = streamReader.getInt();
        } else {
            streamReader.skip(4);
            dataType = this.dataType;
        }
        final int locale = streamReader.getInt();
        final int dataSize = (int)boxHeader.getPayloadSize(false) - 8;
        final Object data;
        switch (dataType) {
            case Data.UTF_8:
                data = new String(streamReader.getBytes(dataSize));
                break;
            case Data.BE_UNSIGNED:
                return StreamUtil.getNumber(streamReader, dataSize, false);
            case Data.BE_SIGNED:
                return StreamUtil.getNumber(streamReader, dataSize, true);
            case Data.SET_INDEX:
                streamReader.getShort(); //Unknown
                return new SetIndex(StreamUtil.getUShort(streamReader), StreamUtil.getUShort(streamReader));
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

    @Override
    public int getType() {
        return BoxTypes.TYPE_data;
    }
}
