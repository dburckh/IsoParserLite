package com.homesoft.iso;

import java.nio.ByteBuffer;

public class UUIDBox extends Box {
    public final ByteBuffer uuid;

    UUIDBox(Number size, int type, byte[] uuid) {
        super(size, type);
        this.uuid = ByteBuffer.wrap(uuid).asReadOnlyBuffer();
    }
}
