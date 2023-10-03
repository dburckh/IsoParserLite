package com.homesoft.iso.box;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.DependencyManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RootContainerBox extends BaseContainerBox implements DependencyManager {
    private final HashMap<Box, List<Listener<Object>>> dependencyMap = new HashMap<>();

    @Override
    public void addDependency(@NonNull Box parent, @NonNull Listener<?> listener) {
        List<Listener<Object>> list = dependencyMap.get(parent);
        if (list == null) {
            list = Collections.singletonList((Listener<Object>)listener);
        } else {
            if (list.size() == 1) {
                list = new ArrayList<>(list);
            }
        }
        dependencyMap.put(parent, list);
    }

    @Override
    public void updateDependencies(@NonNull Box parent, @Nullable Object result) {
        final List<Listener<Object>> list = dependencyMap.get(parent);
        if (list != null) {
            for (Listener<Object> listener : list) {
                listener.onResult(result);
            }
        }
    }
}
