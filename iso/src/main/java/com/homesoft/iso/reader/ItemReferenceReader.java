package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxContainer;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.DependantParser;
import com.homesoft.iso.DependencyManager;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Parser for Box Type iref
 */
public class ItemReferenceReader extends FullBoxReader implements BoxContainer, DependantParser {

    public ItemReferenceReader(DependencyManager dependencyManager) {
        dependencyManager.add(this);
    }

    @Override
    public BoxReader read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = Box.getVersion(versionFlags);
        switch (version) {
            case 0:
                return new SingleItemTypeReferenceReader();
            case 1:
                return new SingleItemTypeReferenceIntReader();
            default:
                return null;
        }
    }

    @Override
    public BoxReader getParser(Box box, Object reader) {
        if (reader instanceof BoxReader) {
            return (BoxReader) reader;
        }
        return null;
    }

    @Override
    public BoxParser getDependantParser() {
        return this;
    }
}
