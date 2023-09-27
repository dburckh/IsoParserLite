package com.homesoft.iso;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TypeListener implements ParseListener {
    private final HashMap<Integer, Object> typeMap = new HashMap<>();
    private final ArrayDeque<Integer> stack = new ArrayDeque<>();
    private final int cancelType;

    private boolean cancelled = false;
    private ArrayList<Object> currentContainer;

    /**
     * Default constructor
     * @param cancelType cause parsing to stop after a {@link ContainerBox} of this type
     *                   has been encountered or {@link BoxTypes#TYPE_NA}
     */
    public TypeListener(int cancelType) {
        this.cancelType = cancelType;
    }

    public void addTypeListeners(int ... types) {
        for (int type : types) {
            typeMap.put(type, null);
        }
    }

    public Object getType(int type) {
        return typeMap.get(type);
    }

    public void clear() {
        for (Map.Entry<Integer, Object> entry : typeMap.entrySet()) {
            entry.setValue(null);
        }
    }

    private void updateCurrentContainer() {
        final Object o = typeMap.get(stack.peek());
        if (o instanceof ArrayList) {
            currentContainer = (ArrayList<Object>) o;
        } else {
            currentContainer = null;
        }
    }

    @Override
    public void onContainerStart(BoxHeader boxHeader, Object result) {
        if (typeMap.containsKey(boxHeader.type)) {
            typeMap.put(boxHeader.type, new ArrayList<>());
        }
        stack.push(boxHeader.type);
        updateCurrentContainer();
    }

    @Override
    public void onParsed(BoxHeader boxHeader, Object result) {
        if (result != null) {
            if (typeMap.containsKey(boxHeader.type)) {
                typeMap.put(boxHeader.type, result);
            }
            if (currentContainer != null) {
                currentContainer.add(result);
            }
        }
    }

    @Override
    public void onContainerEnd(BoxHeader boxHeader) {
        stack.pop();
        updateCurrentContainer();
        if (boxHeader.type == cancelType) {
            cancelled = true;
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
