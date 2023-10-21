package com.homesoft.iso;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An abstract class for a random access {@link StreamReader}
 */
public abstract class RandomStreamReader implements StreamReader {
    private static final int BUFFER_BLOCKS = 2;
    private final ByteBuffer byteBuffer;

    private long block = Long.MIN_VALUE;

    private int reads;

    protected RandomStreamReader(int blockSize) {
        byteBuffer = ByteBuffer.allocateDirect(blockSize * BUFFER_BLOCKS);
        byteBuffer.limit(byteBuffer.position());
    }

    /**
     * Read from the stream and via the backing ByteBuffer.  The {@link #position()} will advance.
     * It's only recommended for reads <= blockSize,
     * for larger reads use {@link #read(ByteBuffer, long)}
     */
    @Override
    public int read(ByteBuffer readBuffer) throws IOException {
        int readRemaining = readBuffer.remaining();
        // Special case 0 to read
        if (readRemaining == 0) {
            return 0;
        }
        final int inPosition = readBuffer.position();
        while (readRemaining > 0) {
            ensureCapacity(readRemaining);
            final int remaining = byteBuffer.remaining();
            if (remaining == 0) {
                break;
            }
            if (remaining > readRemaining) {
                // More bytes in our buffer than read buffer,
                // set limit to prevent BufferOverflowException
                final int inLimit = byteBuffer.limit();
                try {
                    byteBuffer.limit(byteBuffer.position() + readRemaining);
                    readBuffer.put(byteBuffer);
                } finally {
                    byteBuffer.limit(inLimit);
                }
            } else {
                readBuffer.put(byteBuffer);
            }
            readRemaining = readBuffer.remaining();
        }
        // Special case, read on EOF
        if (inPosition == readBuffer.position()) {
            return -1;
        }
        return readBuffer.position() - inPosition;
    }

    /**
     * Mimics {@link java.nio.channels.FileChannel#read(ByteBuffer, long)}
     * Does not affect the internal Buffer or {@link #position()}
     * @return The number of bytes read, possibly zero, or -1 if the channel has reached end-of-stream
     */
    public abstract int read(ByteBuffer byteBuffer, long position) throws IOException;

    public abstract long size() throws IOException;

    private int getBlockSize() {
        return byteBuffer.capacity() / 2;
    }

    public void position(long pos) throws IOException {
        final int blockSize = getBlockSize();
        long newBlock = pos / blockSize;
        if (block != newBlock && block + 1 != newBlock) {
            long readBlock;
            if (newBlock == block + 2) {
                byteBuffer.position(blockSize);
                byteBuffer.compact();
                block++;
                //System.out.println("read: 1");
                readBlock = block + 1;
            } else {
                //System.out.println("read: 2");
                byteBuffer.clear();
                block = readBlock = newBlock;
            }
            int read = read(byteBuffer, readBlock * blockSize);
            reads++;
            if (read > 0) {
                byteBuffer.flip();
            } else {
                byteBuffer.position(byteBuffer.capacity());
            }
        }
        int newPos = (int) (pos - block * blockSize);
        byteBuffer.position(Math.min(newPos, byteBuffer.capacity()));
    }

    private void ensureCapacity(final int bytes) throws IOException {
        if (byteBuffer.remaining() < bytes) {
            long pos = position();
            position(pos + bytes);
            position(pos);
            if (byteBuffer.remaining() < bytes) {
                throw new IOException("BufferOverrun {blockSize=" + getBlockSize() + ", bytes="+bytes+"}");
            }
        }
    }

    @Override
    public byte get() throws IOException {
        ensureCapacity(1);
        return byteBuffer.get();
    }
    @Override
    public short getShort() throws IOException {
        ensureCapacity(2);
        return byteBuffer.getShort();
    }
    @Override
    public int getInt() throws IOException {
        ensureCapacity(4);
        return byteBuffer.getInt();
    }

    @Override
    public long getLong() throws IOException {
        ensureCapacity(8);
        return byteBuffer.getLong();
    }

    @NonNull
    @Override
    public byte[] getBytes(final int size) throws IOException {
        final byte[] buffer = new byte[size];
        int pos = 0;
        while (pos != size) {
            ensureCapacity(1);
            final int length = Math.min(size - pos, byteBuffer.remaining());
            byteBuffer.get(buffer, pos, length);
            pos += length;
        }
        return buffer;
    }

    @Override
    public String getString() throws IOException {
        final ZeroTermString zeroTermString = new ZeroTermString();
        String out;
        while ((out = zeroTermString.appendString(byteBuffer)) == null) {
            ensureCapacity(1);
        }
        return out;
    }

    @Override
    public long position() {
        return block * byteBuffer.capacity() / 2 + byteBuffer.position();
    }

    @Override
    public ByteOrder order() {
        return byteBuffer.order();
    }

    @Override
    public void order(ByteOrder byteOrder) {
        byteBuffer.order(byteOrder);
    }

    @Override
    public long skip(long bytes) throws IOException {
        final long position = position();
        position(position() + bytes);
        return position() - position;
    }


    /**
     * This technically could be larger
     */
    @Override
    public int getMaxBufferSize() {
        return getBlockSize();
    }

    @Override
    public ByteBuffer getSharedBuffer(int requestedSize) throws IOException {
        ensureCapacity(requestedSize);
        return byteBuffer;
    }

}
