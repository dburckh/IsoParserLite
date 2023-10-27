package com.homesoft.iso.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ParseListener;

import java.util.HashMap;

/**
 * A {@link ParseListener} that delegates a {@link com.homesoft.iso.BoxContainer} and it's children to
 * another {@link ParseListener}.
 * This class does not directly support nesting delegates.  Nesting can be supported by multiple
 * CompositeListener instance nested in each other.
 */
public class CompositeListener extends ProxyListener {
    private final HashMap<Integer, ProxyListener> typeMap = new HashMap<>();

    @Nullable
    private ParseListener delegate;
    private int delegateType = BoxTypes.TYPE_NA;

    public CompositeListener(@NonNull ParseListener parseListener) {
        super(parseListener);
    }

    public void add(ProxyListener proxyListener, int type) {
        typeMap.put(type, proxyListener);
    }

    /**
     * Set the {@link ParseListener} for this class and all children
     * @param parseListener
     */
    @Override
    public void setParseListener(@NonNull ParseListener parseListener) {
        super.setParseListener(parseListener);
        for (ProxyListener proxyListener : typeMap.values()) {
            proxyListener.setParseListener(parseListener);
        }
    }

    @NonNull
    private ParseListener peek() {
        if (delegate == null) {
            return getParseListener();
        } else {
            return delegate;
        }
    }

    @Override
    public void onContainerStart(int type) {
        ParseListener parseListener = typeMap.get(type);
        if (parseListener == null) {
            parseListener = peek();
        } else {
            assert delegate == null;
            delegate = parseListener;
            delegateType = type;
        }
        parseListener.onContainerStart(type);
    }

    @Override
    public void onParsed(int type, Object result) {
        peek().onParsed(type, result);
    }

    @Override
    public void onContainerEnd(int type) {
        final ParseListener typedParseListener = peek();
        typedParseListener.onContainerEnd(type);
        if (delegateType == type) {
            delegateType = BoxTypes.TYPE_NA;
            delegate = null;
        }
    }

    @Override
    public boolean isCancelled() {
        return peek().isCancelled();
    }
}
