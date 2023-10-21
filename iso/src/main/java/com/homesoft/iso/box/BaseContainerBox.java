package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ContainerBox;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;
import com.homesoft.iso.UUIDBoxHeader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Generic container parser
 */
public class BaseContainerBox implements ContainerBox {
    /**
     * Wildcard match for boxes
     */
    public static final int TYPE_DEFAULT = BoxTypes.TYPE_NA;
    /**
     * Contains keys of types (Integer) or uuid (ByteBuffer)
     */
    final HashMap<Object, Box> boxMap = new HashMap<>();

    private final boolean fullBox;
    public BaseContainerBox() {
        this(false);
    }
    /**
     * Generic <code>BoxParser</code> for container boxes
     * @param fullBox true if this Box has version and flag data
     *
     * @see ExtentBox
     */
    public BaseContainerBox(boolean fullBox) {
        this.fullBox = fullBox;
    }

    public BaseContainerBox addParser(TypedBox typedBox) {
        return addParser(typedBox.getType(), typedBox);
    }

    /**
     * Add a sub Box to this container keyed on Type
     * @return this instance
     */
    public BaseContainerBox addParser(int type, Box parser) {
        boxMap.put(type, parser);
        return this;
    }
    /**
     * Add a sub Box to this container keyed on UUID
     * @return this instance
     */
    public BaseContainerBox addParser(byte[] uuid, Box parser) {
        boxMap.put(ByteBuffer.wrap(uuid), parser);
        return this;
    }
    @Nullable
    @Override
    public Object read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        return null;
    }

    @Nullable
    @Override
    public Box getBox(BoxHeader boxHeader) {
        Box box = boxHeader instanceof UUIDBoxHeader ?
                boxMap.get(((UUIDBoxHeader) boxHeader).uuid) : boxMap.get(boxHeader.type);
        if (box == null) {
            box = boxMap.get(TYPE_DEFAULT);
        }
        return box;
    }

    @Override
    public boolean isFullBox() {
        return fullBox;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + boxMap +
                '}';
    }
}
