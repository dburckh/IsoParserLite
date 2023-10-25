package com.homesoft.iso.reader.cr3;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;

import java.io.IOException;

public class ThumbnailReader extends FullBoxReader implements TypedParser {
    public static final int TYPE_THMB = 0x54484D42;

    @Nullable
    @Override
    public ImageExtent read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = Box.getVersion(versionFlags);
        return new ImageExtent(CRawVisualSampleEntry.IMAGE_TYPE_JPEG,
                streamReader.getShort(),
                streamReader.getShort(),
                version == 1 ? streamReader.position() + 4: streamReader.position() + 8,
                streamReader.getInt());
    }

    @Override
    public int getType() {
        return TYPE_THMB;
    }
}
