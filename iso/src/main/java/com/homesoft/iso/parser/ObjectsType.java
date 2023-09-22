package com.homesoft.iso.parser;

import androidx.annotation.NonNull;

import com.homesoft.iso.Type;

public class ObjectsType implements Type {
    public final int type;

    public final Object[] objects;

    public ObjectsType(final int type, @NonNull Object[] objects) {
        this.type = type;
        this.objects = objects;
    }

    @Override
    public int getType() {
        return type;
    }
}
