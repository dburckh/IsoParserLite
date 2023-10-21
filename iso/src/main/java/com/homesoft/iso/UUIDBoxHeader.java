package com.homesoft.iso;

import java.nio.ByteBuffer;

public class UUIDBoxHeader extends BoxHeader {
    public final ByteBuffer uuid;

    UUIDBoxHeader(Number size, int type, byte[] uuid) {
        super(size, type);
        this.uuid = ByteBuffer.wrap(uuid).asReadOnlyBuffer();
    }
}
