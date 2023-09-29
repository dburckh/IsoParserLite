package com.homesoft.iso;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public interface StreamReader extends AutoCloseable {

    /**
     * Get a single byte from the stream.  Will perform IO as necessary.
     * The get will succeed or throw an IOException.
     * The {@link #position()} will advance.
     */
    byte get() throws IOException;

    /**
     * Get a short (int16) from the stream.  Will perform IO as necessary.
     * The get will succeed or throw an IOException.
     * The {@link #position()} will advance.
     */
    short getShort() throws IOException;
    /**
     * Get an int (int32) from the stream.  Will perform IO as necessary.
     * The get will succeed or throw an IOException.
     * The {@link #position()} will advance.
     */
    int getInt() throws IOException;
    /**
     * Get a long (int64) from the stream.  Will perform IO as necessary.
     * The get will succeed or throw an IOException.
     * The {@link #position()} will advance.
     */
    long getLong() throws IOException;

    /**
     * Get a zero terminated UTF-8 String from the stream.
     * Will perform IO as necessary.
     * The get will succeed or throw an IOException.
     * The {@link #position()} will advance.
     */
    String getString() throws IOException;

    /**
     * Get a byte[] array from the stream.
     * Will perform IO as necessary.
     * The get will succeed or throw an IOException.
     * The {@link #position()} will advance.
     */
    @NonNull
    byte[] getBytes(int size) throws IOException;

    /**
     * Read into the passed ByteBuffer from the stream.
     * The position() and limit() will be respected.
     * The {@link ByteBuffer#position()} will be advanced.
     * Will perform IO as necessary.
     * The get will succeed or throw an IOException.
     * The {@link #position()} will advance.
     * @return Follows the {@link java.nio.channels.SeekableByteChannel#read(ByteBuffer)} contract.
     */
    int read(ByteBuffer byteBuffer) throws IOException;

    /**
     * The current position in the stream
     */
    long position();

    /**
     * The {@link ByteOrder} of used for {@link #getShort()}, {@link #getInt()} and
     * {@link #getLong()} methods.
     */
    ByteOrder order();

    /**
     * Set the {@link ByteOrder}
     * @param byteOrder
     */
    void order(ByteOrder byteOrder);

    /**
     * Attempt to skip bytes of the stream.
     * @param bytes number of bytes to attempt skipping.
     *              Maybe be negative for {@link RandomStreamReader} implementations.
     * @return number of bytes actually skipped.
     */
    long skip(long bytes) throws IOException;

    /**
     * Get the maximum number of bytes the internal ByteBuffer can hold
     */
    int getMaxBufferSize();

    /**
     * Attempt to get a ByteBuffer of a given requestedSize
     *
     * NOTES:
     * This method is subject to change!
     * Although it's super lightweight and convenient it's also super easy to mess stuff up.
     * Especially by modifying the limit(), which is something that is useful.
     * Maybe a shared ByteBuffer (RO & RW) and verify the limit() internally?
     *
     * At a minimum, in a future release it will be readonly
     *
     * @return ByteBuffer that is directly tied to the StreamReader.
     *         Changes in the BytesBuffer position() will affect the StreamReader position()
     *         The ByteBuffer may not be the full <code>requestedSize</code>
     */
    ByteBuffer getSharedBuffer(int requestedSize) throws IOException;
}
