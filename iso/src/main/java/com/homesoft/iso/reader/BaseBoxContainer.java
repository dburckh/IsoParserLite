package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.BoxContainer;
import com.homesoft.iso.TypedParser;
import com.homesoft.iso.UUIDBox;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Generic container parser
 */
public class BaseBoxContainer implements BoxContainer {
    /**
     * Wildcard match for boxes
     */
    public static final int TYPE_DEFAULT = BoxTypes.TYPE_NA;
    /**
     * Contains keys of types (Integer) or uuid (ByteBuffer)
     */
    final HashMap<Object, BoxParser> boxMap = new HashMap<>();

    public BaseBoxContainer addParser(TypedParser typedParser) {
        return addParser(typedParser.getType(), typedParser);
    }

    /**
     * Add a sub Box to this container keyed on Type
     * @return this instance
     */
    public BaseBoxContainer addParser(int type, BoxParser parser) {
        boxMap.put(type, parser);
        return this;
    }
    /**
     * Add a sub Box to this container keyed on UUID
     * @return this instance
     */
    public BaseBoxContainer addParser(byte[] uuid, BoxParser parser) {
        boxMap.put(ByteBuffer.wrap(uuid), parser);
        return this;
    }

    @Nullable
    @Override
    public BoxParser getParser(Box box, Object dependency) {
        BoxParser paserer = box instanceof UUIDBox ?
                boxMap.get(((UUIDBox) box).uuid) : boxMap.get(box.type);
        if (paserer == null) {
            paserer = boxMap.get(TYPE_DEFAULT);
        }
        return paserer;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + boxMap +
                '}';
    }
}
