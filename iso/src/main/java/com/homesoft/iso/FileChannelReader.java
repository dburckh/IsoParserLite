package com.homesoft.iso;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelReader extends RandomStreamReader {
    private final FileChannel fileChannel;

    public FileChannelReader(FileChannel fileChannel, int blockSize) {
        super(blockSize);
        this.fileChannel = fileChannel;
    }

    @Override
    public int read(ByteBuffer byteBuffer, long position) throws IOException {
        return fileChannel.read(byteBuffer, position);
    }

    @Override
    public long size() throws IOException {
        return fileChannel.size();
    }
    @Override
    public void close() throws IOException {
        fileChannel.close();
    }
}
