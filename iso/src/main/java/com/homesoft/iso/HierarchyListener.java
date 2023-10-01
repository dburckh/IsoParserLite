package com.homesoft.iso;

import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * {@link ParseListener} that records the results in a hierarchy
 * Lists will be stored in a {@link TypedListResult}.
 * Items that are {@link Type} will be stored as is.
 * Other items will be wrapped in a {@link TypedResult}
 */
public class HierarchyListener implements ParseListener, ResultResolver {
    private final ArrayDeque<TypedListResult> stack = new ArrayDeque<>();
    private final TypedListResult root = new TypedListResult(BoxTypes.TYPE_NA, null);
    private final int cancelType;

    private boolean cancelled = false;
    /**
     * Default constructor
     * @param cancelType cause parsing to stop after a {@link ContainerBox} of this type
     *                   has been encountered or {@link BoxTypes#TYPE_NA}
     */
    public HierarchyListener(int cancelType) {
        stack.push(root);
        this.cancelType = cancelType;
    }

    @Override
    public void onContainerStart(BoxHeader boxHeader, Object result) {
        final TypedListResult parentList = Objects.requireNonNull(stack.peek());
        final TypedListResult childList = new TypedListResult(boxHeader.type, result);
        parentList.add(childList);
        stack.push(childList);
    }

    @Override
    public void onParsed(BoxHeader boxHeader, Object result) {
        if (result == null) {
            return;
        }
        final TypedListResult typedList = Objects.requireNonNull(stack.peek());
        final Type type;
        if (result instanceof Type) {
            type = (Type) result;
        } else {
            type = new TypedResult(boxHeader.type, result);
        }
        typedList.list.add(type);
    }

    @Override
    public void onContainerEnd(BoxHeader boxHeader) {
        stack.pop();
        if (boxHeader.type == cancelType) {
            cancelled = true;
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Nullable
    @Override
    public Object getResult(int type) throws NoSuchElementException {
        for (TypedListResult typedListResult : stack) {
            if (typedListResult.type == type) {
                return typedListResult;
            }
            for (Type result : typedListResult.list) {
                if (result.getType() == type) {
                    if (result instanceof TypedResult) {
                        return ((TypedResult) result).result;
                    } else {
                        return result;
                    }
                }
            }
        }
        throw new NoSuchElementException();
    }

    private void toString(ArrayList<Type> list, StringBuilder sb, String indent) {
        for (Type type : list) {
            sb.append(indent);
            sb.append(type.toString());
            sb.append('\n');
            if (type instanceof TypedListResult) {
                toString(((TypedListResult) type).list, sb, indent + "-");
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
        toString(root.list, sb, "");
        return sb.toString();
    }

    public static class TypedResult implements Type {
        public final int type;
        public final Object result;

        public TypedResult(int type, @Nullable Object result) {
            this.type = type;
            this.result = result;
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(BoxHeader.typeToString(type));
            if (result != null) {
                sb.append('=');
                sb.append(result);
            }
           return sb.toString();
        }
    }

    public static class TypedListResult extends TypedResult {
        private final ArrayList<Type> list = new ArrayList<>();

        public TypedListResult(int type, @Nullable Object result) {
            super(type, result);
        }

        public void add(Type type) {
            list.add(type);
        }

        public List<Type> getList() {
            return Collections.unmodifiableList(list);
        }
    }
}
