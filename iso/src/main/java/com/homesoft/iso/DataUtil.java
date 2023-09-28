package com.homesoft.iso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class DataUtil {
    private static final long UINT_MASK = 0xffffffffL;
    public static final int USHORT_MASK = 0xffff;
    private static final int UBYTE_MASK = 0xff;
    public static int getInt(ByteBuffer byteBuffer, int size) {
        switch (size) {
            case 2:
                return byteBuffer.getShort();
            case 4:
                return byteBuffer.getInt();
            case 1:
                return byteBuffer.get();
            default:
                throw new UnsupportedOperationException("Size: " + size);
        }
    }

    public static Number getNumber(StreamReader streamReader, int size, boolean signed) throws IOException {
        switch (size) {
            case 2: {
                final short s = streamReader.getShort();
                if (signed || s >= 0) {
                    return s;
                } else {
                    return s & USHORT_MASK;
                }
            }
            case 4: {
                final int i = streamReader.getInt();
                if (signed || i >= 0) {
                    return i;
                } else {
                    return i & UINT_MASK;
                }
            }
            case 1: {
                final byte b = streamReader.get();
                if (signed || b >= 0) {
                    return b;
                } else {
                    return b & UBYTE_MASK;
                }
            }
        }
        return null;
    }

    public static long getUInt(int i) {
        return i & UINT_MASK;
    }

    public static long getUInt(ByteBuffer byteBuffer) {
        return getUInt(byteBuffer.getInt());
    }

    public static long getUInt(StreamReader streamReader) throws IOException {
        return getUInt(streamReader.getInt());
    }

    public static int getUShort(short s) {
        return s & USHORT_MASK;
    }

    public static int getUShort(ByteBuffer byteBuffer) {
        return getUShort(byteBuffer.getShort());
    }

    public static int getUShort(StreamReader streamReader) throws IOException {
        return getUShort(streamReader.getShort());
    }

    /**
     *
     * @return byte as int.  bitwise and up-converts to int, so we just go with it.
     */
    public static int getUByte(ByteBuffer byteBuffer) {
        return byteBuffer.get() & UBYTE_MASK;
    }

    public static int getUByte(StreamReader streamReader) throws IOException {
        return streamReader.get() & UBYTE_MASK;
    }
    private static int toUInt(long size) {
        if (size > Integer.MAX_VALUE || size < 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        return (int)size;
    }

    public static ByteBuffer getByteBuffer(long lSize, StreamReader streamReader) throws IOException {
        return streamReader.getSharedBuffer(toUInt(lSize));
    }
    public static ByteBuffer requireSharedBuffer(long size, StreamReader streamReader) throws IOException, BufferUnderflowException {
        if (size > streamReader.getMaxBufferSize()) {
            throw new BufferUnderflowException();
        }
        return requireSharedBuffer((int) size, streamReader);
    }
    public static ByteBuffer requireSharedBuffer(int size, StreamReader streamReader) throws IOException, BufferUnderflowException {
        final ByteBuffer byteBuffer = streamReader.getSharedBuffer(size);
        if (byteBuffer.remaining() < size) {
            throw new BufferUnderflowException();
        }
        return byteBuffer;
    }

    /**
     *
     * @param type type searching for
     * @return the first instance of the type or null if not found
     */
    @Nullable
    public static Type findType(int type, @NonNull Object[] objects) {
        for (Object object : objects) {
            if (object instanceof Type && ((Type)object).getType() == type) {
                return (Type) object;
            }
        }
        return null;
    }

    @Nullable
    public static <T> T findClass(Class<T> clazz, @NonNull Object[] objects) {
        for (Object object : objects) {
            if (clazz.isAssignableFrom(object.getClass())) {
                return (T)object;
            }
        }
        return null;
    }
    public static int indexOf(int type, @NonNull Type[] typeArray) {
        for (int i=0;i<typeArray.length;i++) {
            if (typeArray[i].getType() == type) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public static <T extends Id> T findId(int id, @NonNull T[] idArray) {
        if (id <= 0) {
            return null;
        }
        // Id arrays are usually ordered with id 1 at [0], ...
        if (id <= idArray.length) {
            if (idArray[id - 1].getId() == id) {
                return idArray[id - 1];
            }
        }
        // Brute force
        for (T boxType : idArray) {
            if (boxType.getId() == id) {
                return boxType;
            }
        }
        return null;
    }

    /**
     * Blindly attempt to convert an Object to an array of T.
     * The Collection must contain items assignable to T
     * @param object Must contain an {@link java.util.Collection} of T
     * @param c the contents of the collection
     * @return An array of type T
     * @param <T>
     */
    public static <T> T[] toArray(Object object, Class<T> c) throws ClassCastException{
        Collection<T> collection = (Collection) object;
        T[] array = (T[])Array.newInstance(c, collection.size());
        final Iterator<T> it = collection.iterator();
        for (int i=0;it.hasNext();i++) {
            array[i] = it.next();
        }
        return array;
    }
}
