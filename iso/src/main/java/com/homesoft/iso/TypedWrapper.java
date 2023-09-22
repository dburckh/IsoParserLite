package com.homesoft.iso;

import androidx.annotation.NonNull;

public class TypedWrapper implements Type {
    public static final TypedWrapper[] EMPTY_ARRAY = new TypedWrapper[0];

    public final int type;
    @NonNull
    public final Object data;

    public TypedWrapper(int type, @NonNull Object data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "{type=" + Box.typeToString(type) + ", data=" + data +"}";
    }
}
