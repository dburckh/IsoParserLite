package com.homesoft.iso.heif;

import com.homesoft.iso.FileChannelReader;
import com.homesoft.iso.IsoParser;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RandomFileChannelReaderTest {
    @Test
    public void testFragmentedOverRead() throws IOException {
        FileChannelReader fileChannelReader = IsoParser.getFileChannelReader(MovieTest.getSongFile());
        final long size = fileChannelReader.size();
        final int blockSize = fileChannelReader.getBlockSize();
        final long remainder = size % blockSize;
        final long position = size - blockSize * 3;
        fileChannelReader.position(position);
        final ByteBuffer readBuffer = ByteBuffer.allocate(blockSize * 4);
        final int bytes = fileChannelReader.read(readBuffer);
        Assert.assertEquals(blockSize * 3, readBuffer.position());
        final ByteBuffer byteBuffer = fileChannelReader.getSharedBuffer(0);
        Assert.assertEquals(remainder, byteBuffer.position());
        Assert.assertFalse(byteBuffer.hasRemaining());

        final ByteBuffer nativeBuffer = ByteBuffer.allocate(readBuffer.capacity());
        final int nativeBytes = fileChannelReader.read(nativeBuffer, position);

        Assert.assertEquals(nativeBuffer, readBuffer);
        Assert.assertEquals(nativeBytes, bytes);
    }
}
