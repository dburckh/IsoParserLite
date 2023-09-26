package com.homesoft.iso;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TypeListener implements ParseListener {
    private final HashMap<Integer, Object> typeMap = new HashMap<>();
    private final ArrayDeque<Integer> stack = new ArrayDeque<>();

    private ArrayList<Object> currentContainer;

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
    public void onContainerStart(Box box, Object result) {
        if (typeMap.containsKey(box.type)) {
            typeMap.put(box.type, new ArrayList<>());
        }
        stack.push(box.type);
        updateCurrentContainer();
    }

    @Override
    public void onParsed(Box box, Object result) {
        if (result != null) {
            if (typeMap.containsKey(box.type)) {
                typeMap.put(box.type, result);
            }
            if (currentContainer != null) {
                currentContainer.add(result);
            }
        }
    }

    @Override
    public void onContainerEnd(Box box) {
        stack.pop();
        updateCurrentContainer();
    }
}
