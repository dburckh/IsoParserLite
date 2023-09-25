package com.homesoft.iso;

public class TypedResult implements Type {
    public final int type;
    public final Object result;

    public TypedResult(int type, Object result) {
        this.type = type;
        this.result = result;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "{type=" + Box.typeToString(type) + ", result=" + result + "}";
    }
}
