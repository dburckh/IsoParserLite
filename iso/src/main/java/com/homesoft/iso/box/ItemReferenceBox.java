package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.ContainerBox;
import com.homesoft.iso.Box;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Parser for Box Type iref
 */
public class ItemReferenceBox implements ContainerBox {
    public static final int TYPE_DIMG = 0x64696D67; // dimg
    public static final int TYPE_THMB = 0x74686D62; // thmb
    public static final int TYPE_CDSC = 0x63647363; // cdsc
    public static final int TYPE_AUXL = 0x6175786C; // auxl

    private Box box = null;

    @Override
    public boolean isFullBox() {
        return true;
    }

    @Override
    public Void read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final int version = BoxHeader.getVersion(versionFlags);
        switch (version) {
            case 0:
                box = new SingleItemTypeReferenceBox();
                break;
            case 1:
                box = new SingleItemTypeReferenceIntBox();
                break;
            default:
                box = null;
        }
        return null;
    }

    @Override
    public Box getParser(int type) {
        return box;
    }
}
