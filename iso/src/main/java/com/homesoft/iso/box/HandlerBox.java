package com.homesoft.iso.box;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;

public class HandlerBox implements TypedBox {
    public static final int VIDEO = 0x76696465; // vide
    public static final int SOUND = 0x736F756E; // soun
    public static final int HINT = 0x68696E74; // hint
    public static final int META = 0x6D657461; // meta
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Override
    public Integer read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        streamReader.skip(4); // preDefined
        return streamReader.getInt(); // handlerType
        // 4 * 3 reserved
        // sztName
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_hdlr;
    }
}
