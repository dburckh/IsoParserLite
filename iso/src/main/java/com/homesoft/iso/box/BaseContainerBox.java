package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ContainerBox;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.util.HashMap;

/**
 * Generic container parser
 */
public class BaseContainerBox implements ContainerBox {
    /**
     * Wildcard match for boxes
     */
    public static final int TYPE_DEFAULT = BoxTypes.TYPE_NA;
    final HashMap<Integer, Box> parserMap = new HashMap<>();

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

    public BaseContainerBox addParser(int type, Box parser) {
        parserMap.put(type, parser);
        return this;
    }
    @Nullable
    @Override
    public Object read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        return null;
    }

    @Nullable
    @Override
    public Box getBox(int type) {
        Box box = parserMap.get(type);
        if (box == null) {
            box = parserMap.get(TYPE_DEFAULT);
        }
        return box;
    }

    @Override
    public boolean isFullBox() {
        return fullBox;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + parserMap +
                '}';
    }
}
