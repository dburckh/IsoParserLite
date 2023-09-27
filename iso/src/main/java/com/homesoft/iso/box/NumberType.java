package com.homesoft.iso.box;

import com.homesoft.iso.Type;

public class NumberType implements Type {
    public final int type;
    public final Number number;

    public NumberType(int type, Number number) {
        this.type = type;
        this.number = number;
    }

    @Override
    public int getType() {
        return type;
    }
}
