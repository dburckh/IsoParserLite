package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.DependantParser;
import com.homesoft.iso.DependencyManager;
import com.homesoft.iso.FullBoxContainer;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class SampleDescriptionReader extends FullBoxContainer implements DependantParser {
    private final HandlerReader handlerReader;
    public SampleDescriptionReader(DependencyManager dependencyManager, HandlerReader handlerReader) {
        this.handlerReader = handlerReader;
        dependencyManager.add(handlerReader);
   }

    @Nullable
    @Override
    public Integer read(Box box, StreamReader streamReader) throws IOException {
        super.read(box, streamReader);
        return streamReader.getInt(); // entryCount
    }

    @Nullable
    @Override
    public BoxParser getParser(Box box, Object handler) {
        // Try get a specific parser first
        BoxParser parser = super.getParser(box, null);
        if (parser == null && handler instanceof Integer) {
            // If we fail, try to get one by handler
            parser = getParser(new Box(box.getSize(), (Integer)handler), null);
        }
        return parser;
    }

    @Override
    public BoxParser getDependantParser() {
        return handlerReader;
    }
}
