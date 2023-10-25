package com.homesoft.iso.reader;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.FullBoxReader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedParser;

import java.io.IOException;

public class HandlerReader extends FullBoxReader implements TypedParser {
    public static final int VIDEO = 0x76696465; // vide
    public static final int SOUND = 0x736F756E; // soun
    public static final int HINT = 0x68696E74; // hint
    public static final int META = 0x6D657461; // meta

    @Override
    public Integer read(Box box, StreamReader streamReader, int versionFlags) throws IOException {
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
