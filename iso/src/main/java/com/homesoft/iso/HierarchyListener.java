package com.homesoft.iso;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;

/**
 * {@link ParseListener} that records the results in a hierarchy
 * Lists will be stored in a {@link TypedList<Type>}.
 * Items that are {@link Type} will be stored as is.
 * Other items will be wrapped in a {@link TypedResult}
 */
public class HierarchyListener extends ContainerListener {

    @Override
    public void onContainerStart(Box box, Object result) {
        final TypedList<Type> parentContainer = getTopContainer();
        super.onContainerStart(box, result);
        parentContainer.add(getTopContainer());
    }

    @Override
    public void onParsed(Box box, Object result) {
        if (result == null) {
            return;
        }
        final TypedList<Type> typedList = getTopContainer();
        final Type type;
        if (result instanceof Type) {
            type = (Type) result;
        } else {
            type = new TypedResult(box.type, result);
        }
        typedList.list.add(type);
    }
//    public String toString() {
//        return rootList.toString();
//    }
}
