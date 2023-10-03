package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Box;
import com.homesoft.iso.DependencyManager;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class SampleDescriptionBox extends BaseContainerBox implements DependencyManager.Listener<Integer> {
    private Integer handler;

    public SampleDescriptionBox(DependencyManager dependencyManager, HandlerBox handlerBox) {
        super(true);
        dependencyManager.addDependency(handlerBox, this);
   }

    @Override
    public void onResult(Integer result) {
        handler = result;
    }

    @Nullable
    @Override
    public Integer read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        return streamReader.getInt(); // entryCount
    }

    @Nullable
    @Override
    public Box getBox(int type) {
        // Try get a specific parser first
        Box box = super.getBox(type);
        if (box == null && handler != null) {
            // If we fail, try to get one by handler
            box = super.getBox(handler);
        }
        return box;
    }
}
