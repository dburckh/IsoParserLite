package com.homesoft.iso.box.cr3;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PreviewBox implements TypedBox {
    public static final int TYPE_PRVW = 0x50525657;
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Nullable
    @Override
    public JpegImage read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        streamReader.skip(2);
        ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(0xc, streamReader);
        short width = byteBuffer.getShort();
        short height = byteBuffer.getShort();
        byteBuffer.getShort(); // Unknown
        return new JpegImage(width, height, streamReader.position(), byteBuffer.getInt());
    }

    @Override
    public int getType() {
        return TYPE_PRVW;
    }
}
