package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxParser;
import com.homesoft.iso.DependencyManager;

import java.util.HashSet;

public class RootContainerReader extends BaseBoxContainer implements DependencyManager {
    private final HashSet<BoxParser> dependencySet = new HashSet<>();

    @Override
    public void add(@NonNull BoxParser parent) {
        dependencySet.add(parent);
    }

    @Override
    public boolean contains(BoxParser boxReader) {
        return dependencySet.contains(boxReader);
    }
}
