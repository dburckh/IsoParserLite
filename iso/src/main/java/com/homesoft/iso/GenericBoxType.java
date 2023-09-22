package com.homesoft.iso;

/**
 * Used for an unknown Box type
 */
public class GenericBoxType implements Type {
    public final int type;
    public final long start;

    public final long size;
    public GenericBoxType(int type, long start, long size) {
        this.type = type;
        this.start = start;
        this.size = size;
    }

    @Override
    public int getType() {
        return type;
    }
}
