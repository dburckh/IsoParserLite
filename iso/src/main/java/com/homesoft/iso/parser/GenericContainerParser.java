package com.homesoft.iso.parser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.ContainerParser;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBoxParser;

import java.io.IOException;
import java.util.HashMap;

/**
 * Generic container parser
 */
public class GenericContainerParser implements ContainerParser {
    private static final ExtentBoxParser EXTENT_BOX_PARSER = new ExtentBoxParser();
    final HashMap<Integer, BoxParser> parserMap = new HashMap<>();

    private final boolean fullBox;
    private final boolean includeUnknown;

    public GenericContainerParser() {
        this(false, false);
    }
    /**
     * Generic <code>BoxParser</code> for container boxes
     * @param fullBox true if this Box has version and flag data
     * @param includeUnknown true if unknown boxes should be parsed a {@link Extent}
     *
     * @see ExtentBoxParser
     */
    public GenericContainerParser(boolean fullBox, boolean includeUnknown) {
        this.fullBox = fullBox;
        this.includeUnknown = includeUnknown;
    }

    public GenericContainerParser addParser(TypedBoxParser typedBoxParser) {
        return addParser(typedBoxParser.getType(), typedBoxParser);
    }

    public GenericContainerParser addParser(int type, BoxParser parser) {
        parserMap.put(type, parser);
        return this;
    }
    @NonNull
    @Override
    public Void parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        return null;
    }

    @Nullable
    @Override
    public BoxParser getParser(int type) {
        final BoxParser boxParser = parserMap.get(type);
        if (boxParser == null && includeUnknown) {
            return EXTENT_BOX_PARSER;
        }
        return boxParser;
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
