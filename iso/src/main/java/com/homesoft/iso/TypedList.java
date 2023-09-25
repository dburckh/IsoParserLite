package com.homesoft.iso;

import java.util.ArrayList;
import java.util.List;

public class TypedList<T> implements Type {
    public final int type;
    public final List<T> list;

    public TypedList(int type, List<T> list) {
        this.type = type;
        this.list = list;
    }

    public TypedList(int type) {
        this(type, new ArrayList<>());
    }

    @Override
    public int getType() {
        return type;
    }

    public void add(T t) {
        list.add(t);
    }

    public void clear() {
        list.clear();
    }

    String toString(String indent) {
        final StringBuilder sb = new StringBuilder();
        for (T t : list) {
            sb.append(indent);
            sb.append(t.toString());
            if (t instanceof TypedList) {
                sb.append(((TypedList<?>) t).toString(indent + " "));
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{type=");
        sb.append(Box.typeToString(type));
        sb.append(", children=[");
        for (T t : list) {
            sb.append(t);
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
