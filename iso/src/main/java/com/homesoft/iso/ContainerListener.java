package com.homesoft.iso;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;

public abstract class ContainerListener implements ParseListener {
    public static final int ROOT_TYPE = 0;
    private final TypedList<Type> rootList = new TypedList<>(ROOT_TYPE);

    private final ArrayDeque<TypedList<Type>> containerStack = new ArrayDeque<>();

    protected ContainerListener() {
        containerStack.add(rootList);
    }

    public void clear() {
        rootList.clear();
    }

    /**
     * Get the current container as an {@link TypedList<Type>}
     * May be the special ROOT_TYPE
     */
    @NonNull
    protected TypedList<Type> getTopContainer() {
        final TypedList<Type> top = containerStack.peek();
        if (top == null) {
            throw new RuntimeException("Internal Error.  Stack must have at least 1 Type");
        }
        return top;
    }
    @Override
    public void onContainerStart(Box box, Object result) {
        final TypedList<Type> child = new TypedList<>(box.type);
        containerStack.push(child);
    }

    @Override
    public void onContainerEnd(Box box) {
        final TypedList<Type> childList = containerStack.pop();
        if (childList.type != box.type) {
            throw new RuntimeException("Internal Error");
        }
    }
}
