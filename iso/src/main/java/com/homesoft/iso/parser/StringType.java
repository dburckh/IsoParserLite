package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
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
        return "StringType{type="+ Box.typeToString(type)+", string=" + string +"}";
    }
}
