package com.homesoft.iso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DataUtil {
    public static final long UINT_MASK = 0xffffffffL;
    public static final int USHORT_MASK = 0xffff;
    public static final int UBYTE_MASK = 0xff;
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

    public static long getUInt(ByteBuffer byteBuffer) {
        return byteBuffer.getInt() & UINT_MASK;
    }

    public static int getUShort(ByteBuffer byteBuffer) {
        return byteBuffer.getShort() & USHORT_MASK;
    }

    public static int getUShort(StreamReader streamReader) throws IOException {
        return streamReader.getShort() & USHORT_MASK;
    }

    public static long getUInt(StreamReader streamReader) throws IOException {
        return streamReader.getInt() & UINT_MASK;
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

    /**
     * Do a recursive search for a given type
     * @param typeArray and array of types than may have a sub
     * @param path
     * @return
     */
    @Nullable
    public static Type getType(Type[] typeArray, int ... path) {
        int i=0;
        Type type;
        while (i<path.length) {
            final int t = path[i];
            type = findType(t, typeArray);
            if (type == null) {
                break;
            }
            if (type instanceof TypedWrapper) {
                final TypedWrapper typedWrapper = (TypedWrapper)type;
                if (typedWrapper.data instanceof Type[]) {
                    typeArray = (Type[]) typedWrapper.data;
                } else {
                    break;
                }
            } else {
                break;
            }
            i++;
        }
        return null;
    }
}
