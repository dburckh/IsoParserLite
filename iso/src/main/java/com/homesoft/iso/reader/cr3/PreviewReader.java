package com.homesoft.iso.reader.cr3;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.TypedParser;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PreviewReader extends FullBoxReader implements TypedParser {
    public static final int TYPE_PRVW = 0x50525657;

    @Nullable
    @Override
    public ImageExtent read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        streamReader.skip(2);
        ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(0xc, streamReader);
        short width = byteBuffer.getShort();
        short height = byteBuffer.getShort();
        byteBuffer.getShort(); // Unknown
        return new ImageExtent(CRawVisualSampleEntry.IMAGE_TYPE_JPEG,
                width, height, streamReader.position() + 4, byteBuffer.getInt());
    }

    @Override
    public int getType() {
        return TYPE_PRVW;
    }
}
