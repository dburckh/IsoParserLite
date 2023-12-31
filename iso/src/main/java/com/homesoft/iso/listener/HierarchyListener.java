package com.homesoft.iso.listener;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.BoxContainer;
import com.homesoft.iso.ParseListener;
import com.homesoft.iso.Type;
import com.homesoft.iso.TypedResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * {@link ParseListener} that records the results in a hierarchy
 * Lists will be stored in a {@link TypedListResult}.
 * Items that are {@link Type} will be stored as is.
 * Other items will be wrapped in a {@link TypedResult}
 */
public class HierarchyListener implements ParseListener {
    private final ArrayDeque<TypedListResult> stack = new ArrayDeque<>();
    private final TypedListResult root = new TypedListResult(BoxTypes.TYPE_NA);
    private final int cancelType;

    private boolean cancelled = false;

    public HierarchyListener() {
        this(BoxTypes.TYPE_NA);
    }
    /**
     * Default constructor
     * @param cancelType cause parsing to stop after a {@link BoxContainer} of this type
     *                   has been encountered or {@link BoxTypes#TYPE_NA}
     */
    public HierarchyListener(int cancelType) {
        stack.push(root);
        this.cancelType = cancelType;
    }

    @Override
    public void onContainerStart(int type) {
        final TypedListResult parentList = Objects.requireNonNull(stack.peek());
        final TypedListResult childList = new TypedListResult(type);
        parentList.add(childList);
        stack.push(childList);
    }

    @Override
    public void onParsed(int type, Object result) {
        if (result == null) {
            return;
        }
        final TypedListResult typedList = Objects.requireNonNull(stack.peek());
        final Type resultType;
        if (result instanceof Type) {
            resultType = (Type) result;
        } else {
            resultType = new TypedResult(type, result);
        }
        typedList.add(resultType);
    }

    @Override
    public void onContainerEnd(int type) {
        stack.pop();
        if (type == cancelType) {
            cancelled = true;
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    private void toString(List<Type> list, StringBuilder sb, String indent) {
        for (Type type : list) {
            sb.append(indent);
            sb.append(type.toString());
            sb.append('\n');
            if (type instanceof TypedListResult) {
                toString(((TypedListResult) type).getRawList(), sb, indent + "-");
            }
        }
    }

    /**
     * Pretty print the hierarchy.
     * Contains new lines.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        toString(root.getRawList(), sb, "");
        return sb.toString();
    }

    public static class TypedListResult extends TypedResult {

        public TypedListResult(int type) {
            super(type, new ArrayList<>());
        }

        @NonNull
        ArrayList<Type> getRawList() {
            return (ArrayList<Type>)result;
        }

        public void add(Type type) {
            getRawList().add(type);
        }

        public List<Type> getList() {
            return Collections.unmodifiableList(getRawList());
        }
    }
}
