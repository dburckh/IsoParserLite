package com.homesoft.iso;

import java.util.ArrayDeque;
import java.util.Objects;

/**
 * {@link ParseListener} that records the results in a hierarchy
 * Lists will be stored in a {@link TypedList<Type>}.
 * Items that are {@link Type} will be stored as is.
 * Other items will be wrapped in a {@link TypedResult}
 */
public class HierarchyListener implements ParseListener {
    private final ArrayDeque<TypedList<Type>> stack = new ArrayDeque<>();

    public HierarchyListener() {
        stack.push(new TypedList<>(0));
    }

    @Override
    public void onContainerStart(Box box, Object result) {
        final TypedList<Type> parentList = Objects.requireNonNull(stack.peek());
        final TypedList<Type> childList = new TypedList<>(box.type);
        parentList.add(childList);
    }

    @Override
    public void onParsed(Box box, Object result) {
        if (result == null) {
            return;
        }
        final TypedList<Type> typedList = Objects.requireNonNull(stack.peek());
        final Type type;
        if (result instanceof Type) {
            type = (Type) result;
        } else {
            type = new TypedResult(box.type, result);
        }
        typedList.list.add(type);
    }

    @Override
    public void onContainerEnd(Box box) {
        stack.pop();
    }
}
