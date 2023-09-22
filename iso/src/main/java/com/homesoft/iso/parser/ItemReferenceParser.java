package com.homesoft.iso.parser;

import com.homesoft.iso.Box;
import com.homesoft.iso.ContainerParser;
import com.homesoft.iso.BoxParser;
import com.homesoft.iso.DataUtil;
import com.homesoft.iso.IsoParser;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Parser for Box Type iref
 */
public class ItemReferenceParser implements ContainerParser {
    public static final int TYPE_DIMG = 0x64696D67; // dimg
    public static final int TYPE_THMB = 0x74686D62; // thmb
    public static final int TYPE_CDSC = 0x63647363; // cdsc
    public static final int TYPE_AUXL = 0x6175786C; // auxl

    private BoxParser boxParser = null;

    @Override
    public boolean isFullBox() {
        return true;
    }

    @Override
    public SingleItemTypeReference[] parse(Box box, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = Box.getVersion(versionFlags);
        final long end = DataUtil.getBoxEnd(box, isFullBox(), streamReader);
        switch (version) {
            case 0:
                boxParser = new SingleItemTypeReferenceParser();
                break;
            case 1:
                boxParser = new SingleItemTypeReferenceIntParser();
                break;
            default:
                return null;

        }
        final Object[] objects = IsoParser.parse(this, streamReader, end);
        return DataUtil.copyArray(objects, SingleItemTypeReference.class);
    }

    @Override
    public BoxParser getParser(int type) {
        return boxParser;
    }
}
