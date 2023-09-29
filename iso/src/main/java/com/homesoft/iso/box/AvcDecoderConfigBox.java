package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Adapted from <a href="https://github.com/gpac/gpac/blob/master/src/isomedia/avc_ext.c">GPAC</a>
 */
public class AvcDecoderConfigBox implements Box {
    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public AvcDecoderConfig read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(boxHeader.getPayloadSize(isFullBox()), streamReader);
        final byte configurationVersion = byteBuffer.get();
        final byte profileIndication = byteBuffer.get();
        final byte profileCompatibility = byteBuffer.get();
        final byte levelIndication = byteBuffer.get();
        final byte nalUnitSize = (byte)(1 + (byteBuffer.get() & 3));
        final int spsCount = byteBuffer.get() & 0x1f;
        final byte[][] spsArray = getConfigs(spsCount, byteBuffer);
        final byte[][] ppsArray = getConfigs(StreamUtil.getUByte(byteBuffer), byteBuffer);
        return new AvcDecoderConfig(configurationVersion, profileIndication, profileCompatibility,
                levelIndication, nalUnitSize, spsArray, ppsArray);
    }

    private byte[][] getConfigs(final int count, ByteBuffer byteBuffer) {
        final byte[][] array = new byte[count][];
        for (int i=0;i<count;i++) {
            final int size = StreamUtil.getUShort(byteBuffer);
            final byte[] config = new byte[size];
            byteBuffer.get(config);
            array[i] = config;
        }
        return array;
    }
}
