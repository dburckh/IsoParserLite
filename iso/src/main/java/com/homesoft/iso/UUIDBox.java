package com.homesoft.iso;

import java.nio.ByteBuffer;

public class UUIDBox extends Box {
    public static final int UUID_SIZE = 16;
    public final ByteBuffer uuid;

    UUIDBox(Number size, int type, byte[] uuid) {
        super(size, type);
        this.uuid = ByteBuffer.wrap(uuid).asReadOnlyBuffer();
    }

    @Override
    public int getHeaderSize(boolean fullBox) {
        return super.getHeaderSize(fullBox) + UUID_SIZE;
    }
}
