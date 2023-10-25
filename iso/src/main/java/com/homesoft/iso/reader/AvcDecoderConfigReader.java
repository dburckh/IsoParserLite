package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Adapted from <a href="https://github.com/gpac/gpac/blob/master/src/isomedia/avc_ext.c">GPAC</a>
 */
public class AvcDecoderConfigReader implements BoxReader {
    @Override
    public AvcDecoderConfig read(Box box, StreamReader streamReader) throws IOException {
        final ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(box.getPayloadSize(false), streamReader);
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
