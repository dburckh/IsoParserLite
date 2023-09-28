package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.ContainerBox;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.util.HashMap;

/**
 * Generic container parser
 */
public class BaseContainerBox implements ContainerBox {
    private static final ExtentBox EXTENT_BOX_PARSER = new ExtentBox();
    final HashMap<Integer, Box> parserMap = new HashMap<>();

    private final boolean fullBox;
    private final boolean includeUnknown;

    public BaseContainerBox() {
        this(false, false);
    }
    /**
     * Generic <code>BoxParser</code> for container boxes
     * @param fullBox true if this Box has version and flag data
     * @param includeUnknown true if unknown boxes should be parsed a {@link Extent}
     *
     * @see ExtentBox
     */
    public BaseContainerBox(boolean fullBox, boolean includeUnknown) {
        this.fullBox = fullBox;
        this.includeUnknown = includeUnknown;
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
        final Box box = parserMap.get(type);
        if (box == null && includeUnknown) {
            return EXTENT_BOX_PARSER;
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
