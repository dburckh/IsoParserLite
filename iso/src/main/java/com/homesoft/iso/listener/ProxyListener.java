package com.homesoft.iso.listener;

import androidx.annotation.NonNull;

import com.homesoft.iso.ParseListener;

public abstract class ProxyListener implements ParseListener {
    @NonNull
    private ParseListener parseListener;


    public ProxyListener(@NonNull ParseListener parseListener) {
        this.parseListener = parseListener;
    }

    public void setParseListener(@NonNull ParseListener parseListener) {
        this.parseListener = parseListener;
    }

    @NonNull
    public ParseListener getParseListener() {
        return parseListener;
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
