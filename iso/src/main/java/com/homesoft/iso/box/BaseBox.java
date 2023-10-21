package com.homesoft.iso.box;

import com.homesoft.iso.Box;

public abstract class BaseBox implements Box {
    private final boolean fullBox;

    protected BaseBox(boolean fullBox) {
        this.fullBox = fullBox;
    }

    @Override
    public boolean isFullBox() {
        return fullBox;
    }
}
