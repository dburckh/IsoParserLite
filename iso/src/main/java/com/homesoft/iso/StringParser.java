package com.homesoft.iso;

import androidx.annotation.NonNull;

import java.io.IOException;

public class StringParser extends IsoParser<String> {
    public StringParser(@NonNull ContainerBox rootContainerBox, HierarchyListener parseListener) {
        super(rootContainerBox, parseListener);
    }

    @Override
    public String parse(@NonNull StreamReader streamReader) throws IOException {
        parseImpl(streamReader);
        return parseListener.toString();
    }
}
