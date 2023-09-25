package com.homesoft.iso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TypeListener extends ContainerListener {
    private final HashMap<Integer, Object> typeMap = new HashMap<>();
    private ArrayList<Object> currentContainer;

    public void addTypeListeners(int ... types) {
        for (int type : types) {
            typeMap.put(type, null);
        }
    }

    public Object getType(int type) {
        return typeMap.get(type);
    }

    @Override
    public void clear() {
        super.clear();
        for (Map.Entry<Integer, Object> entry : typeMap.entrySet()) {
            entry.setValue(null);
        }
    }

    private void updateCurrentContainer() {
        final Object o = typeMap.get(getTopContainer().getType());
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
        super.onContainerStart(box, result);
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
        super.onContainerEnd(box);
        updateCurrentContainer();
    }
}
