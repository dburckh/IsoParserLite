package com.homesoft.iso;

import androidx.annotation.Nullable;

/**
 * Utility class for wrapping a result with a type
 */
public class TypedResult implements Type {
    public final int type;
    public final Object result;

    public TypedResult(int type, @Nullable Object result) {
        this.type = type;
        this.result = result;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Box.typeToString(type));
        if (result != null) {
            StreamUtil.append(sb, result);
        }
        return sb.toString();
    }
}
