package com.homesoft.iso.listener;

import androidx.annotation.NonNull;

import com.homesoft.iso.ParseListener;
import com.homesoft.iso.TypedParseListener;

import java.util.ArrayList;

/**
 * Creates a synthetic type of List&lt;Object&gt; for a container
 * {@link ParseListener#onContainerStart(int, Object)} and {@link ParseListener#onContainerEnd(int)}
 * are both called all other {@link ParseListener} called backs are consume, including sub containers.
 * A single {@link ParseListener#onParsed(int, Object)} will be called for the synthetic element
 * <p>Visually:</p>
 * <p>onContainerStart(containerType, containerResult)</p>
 * <p>onParse(syntheticType, List&lt;Object&gt;)</p>
 * <p>onContainerEnd(containerType)</p>
 */
public class ListListener implements TypedParseListener {
    private final ParseListener parseListener;

    private final int containerType;
    private final int syntheticType;

    private final ArrayList<Object> list = new ArrayList<>();

    private int depth = -1;

    public ListListener(@NonNull ParseListener parseListener, int containerType, int syntheticType) {
        this.parseListener = parseListener;
        this.containerType = containerType;
        this.syntheticType = syntheticType;
    }

    public int getSyntheticType() {
        return syntheticType;
    }

    @Override
    public void onContainerStart(int type, Object result) {
        if (type == getType()) {
            depth = 0;
        } else if (depth >= 0) {
            depth++;
        }
    }

    @Override
    public void onParsed(int type, Object result) {
        if (depth == 0) {
            list.add(result);
        }
    }

    @Override
    public void onContainerEnd(int type) {
        if (depth == 0) {
            parseListener.onParsed(syntheticType, list);
        }
        depth--;
    }

    @Override
    public boolean isCancelled() {
        return parseListener.isCancelled();
    }

    @Override
    public int getType() {
        return containerType;
    }
}
