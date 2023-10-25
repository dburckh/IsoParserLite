package com.homesoft.iso.listener;

import com.homesoft.iso.ParseListener;

public abstract class ProxyListener implements ParseListener {
    private final ParseListener parseListener;

    public ProxyListener(ParseListener parseListener) {
        this.parseListener = parseListener;
    }
    @Override
    public void onContainerStart(int type) {
        parseListener.onContainerStart(type);
    }

    @Override
    public void onParsed(int type, Object result) {
        parseListener.onParsed(type, result);
    }

    @Override
    public void onContainerEnd(int type) {
        parseListener.onContainerEnd(type);
    }

    @Override
    public boolean isCancelled() {
        return parseListener.isCancelled();
    }
}
