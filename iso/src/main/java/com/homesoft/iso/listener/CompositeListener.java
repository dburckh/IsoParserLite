package com.homesoft.iso.listener;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ParseListener;
import com.homesoft.iso.TypedParseListener;

import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * A {@link ParseListener} that delegates a {@link com.homesoft.iso.BoxContainer} and it's children to
 * another {@link ParseListener}
 */
public class CompositeListener implements ParseListener {
    private final HashMap<Integer, TypedParseListener> typeMap = new HashMap<>();

    private final ArrayDeque<TypedParseListener> stack = new ArrayDeque<>();

    /**
     *
     * @param defaultListener The listener that receives all output
     */
    public CompositeListener(@NonNull ParseListener defaultListener) {
        final TypedProxyListener typedProxyListener;
        if (defaultListener instanceof TypedProxyListener) {
            typedProxyListener = (TypedProxyListener) defaultListener;
        } else {
            typedProxyListener = new TypedProxyListener(defaultListener, BoxTypes.TYPE_NA);
        }
        stack.add(typedProxyListener);
    }

    public void add(TypedParseListener typedParseListener) {
        typeMap.put(typedParseListener.getType(), typedParseListener);
    }

    public void add(ParseListener parseListener, int type) {
        add(new TypedProxyListener(parseListener, type));
    }

    @NonNull
    private TypedParseListener peek() {
        TypedParseListener typedParseListener = stack.peek();
        if (typedParseListener == null) {
            throw new InternalError("Stack should never be empty");
        }
        return typedParseListener;
    }

    @Override
    public void onContainerStart(int type) {
        TypedParseListener typedParseListener = typeMap.get(type);
        if (typedParseListener != null) {
            stack.push(typedParseListener);
        } else {
            typedParseListener = peek();
        }
        typedParseListener.onContainerStart(type);
    }

    @Override
    public void onParsed(int type, Object result) {
        peek().onParsed(type, result);
    }

    @Override
    public void onContainerEnd(int type) {
        final TypedParseListener typedParseListener = peek();
        typedParseListener.onContainerEnd(type);
        if (typedParseListener.getType() == type) {
            stack.pop();
        }
    }

    @Override
    public boolean isCancelled() {
        return peek().isCancelled();
    }
}
