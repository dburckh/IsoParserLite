package com.homesoft.iso.box;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FileTypeBox implements TypedBox {
    public static final int TYPE_HEIC = 0x68656963;
    public static final int TYPE_AVIF = 0x61766966;

    @Override
    public int getType() {
        return BoxTypes.TYPE_ftyp;
    }

    @Override
    public boolean isFullBox() {
        return false;
    }

    @NonNull
    @Override
    public FileType read(BoxHeader boxHeader, StreamReader streamReader, int unused) throws IOException {
        final int payloadSize = (int) boxHeader.getPayloadSize(isFullBox());
        final ByteBuffer byteBuffer = streamReader.getSharedBuffer(payloadSize);
        final int major = byteBuffer.getInt();
        final int minor = byteBuffer.getInt();
        final int compatibleCount = (payloadSize - 8) / 4;
        final int[] compatibles = new int[compatibleCount];
        for (int i=0;i<compatibleCount;i++) {
            compatibles[i] = byteBuffer.getInt();
        }
        return new FileType(major, minor, compatibles);
    }
}
