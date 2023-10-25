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
    public static final int TYPE_DIMG = 0x64696D67; // dimg
    public static final int TYPE_THMB = 0x74686D62; // thmb
    public static final int TYPE_CDSC = 0x63647363; // cdsc
    public static final int TYPE_AUXL = 0x6175786C; // auxl

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
