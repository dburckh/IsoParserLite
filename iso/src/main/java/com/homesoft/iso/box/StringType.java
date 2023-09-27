package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Type;

public class StringType implements Type {
    public final int type;
    public final String string;

    public StringType(int type, String s) {
        this.type = type;
        string = s;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "StringType{type="+ BoxHeader.typeToString(type)+", string=" + string +"}";
    }
}
