package com.homesoft.iso.listener;

import com.homesoft.iso.ParseListener;
import com.homesoft.iso.TypedParseListener;

public class TypedProxyListener extends ProxyListener implements TypedParseListener {
    private final int type;

    public TypedProxyListener(ParseListener parseListener, int type) {
        super(parseListener);
        this.type = type;
    }

    @Override
    public int getType() {
        return type;
    }
}
