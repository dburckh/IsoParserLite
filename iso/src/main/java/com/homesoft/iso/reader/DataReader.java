package com.homesoft.iso.reader;


import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;

import java.io.IOException;

public class DataReader implements TypedParser, BoxReader {
    /**
     * Used as an override
     */
    private final int dataType;

    public DataReader() {
        this(BoxTypes.TYPE_NA);
    }

    /**
     * Force a dataType
     * This is necessary because some iTunes types are "well known" and have 0 for dataType
     * @param dataType from {@link Data}
     */
    public DataReader(int dataType) {
        this.dataType = dataType;
    }

    @Nullable
    @Override
    public Object read(Box box, StreamReader streamReader) throws IOException {
        final int dataType;
        if (this.dataType == BoxTypes.TYPE_NA) {
            dataType = streamReader.getInt();
        } else {
            streamReader.skip(4);
            dataType = this.dataType;
        }
        final int locale = streamReader.getInt();
        final int dataSize = (int) box.getPayloadSize(false) - 8;
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
        return new Data(box.type, dataType, locale, data);
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_data;
    }
}
