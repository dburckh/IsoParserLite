package com.homesoft.iso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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

    public static ByteBuffer requireByteBuffer(long lSize, StreamReader streamReader) throws IOException {
        final int size = toUInt(lSize);
        final ByteBuffer byteBuffer = streamReader.getSharedBuffer(size);
        if (byteBuffer.remaining() < size) {
            throw new BufferOverflowException();
        }
        return byteBuffer;
    }

    /**
     * Assumes the stream is positioned after the end of the box header
     * @param box
     * @param fullBox
     * @param streamReader
     * @return
     * @throws IOException
     */
    public static long getBoxEnd(Box box, boolean fullBox, StreamReader streamReader) throws IOException {
        if (box.getSize() == Box.SIZE_EOF) {
            if (streamReader instanceof RandomStreamReader) {
                return ((RandomStreamReader)streamReader).size();
            }
        }
        return streamReader.position() + box.getPayloadSize(fullBox);
    }

    @Nullable
    public static <T> T getTypedWrapperData(@Nullable final Type type, T defaultValue) {
        if (type instanceof TypedWrapper) {
            final TypedWrapper typedWrapper = (TypedWrapper) type;
            if (defaultValue.getClass().isAssignableFrom(typedWrapper.data.getClass())) {
                return (T) typedWrapper.data;
            }
        }
        return defaultValue;
    }

    @NonNull
    public static <T extends Type> List<T> findTypeList(int findType, T[] types) {
        final ArrayList<T> list = new ArrayList<>();
        for (T t : types) {
            if (t.getType() == findType) {
                list.add(t);
            }
        }
        return list;
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

    /**
     * Copy a generic array of Type to a specific subclass of Type
     */
    public static <T> T[] copyArray(Object[] objects, Class<T> clazz) {
        final T[] specificArray = (T[])Array.newInstance(clazz, objects.length);
        for (int i=0;i<objects.length;i++) {
            final Object object = objects[i];
            if (clazz.equals(object.getClass())) {
                specificArray[i] = (T)object;
            }
        }
        return specificArray;
    }
}
