package com.homesoft.iso;

public class UUIDBox extends Box {
    private final byte[] uuid;

    UUIDBox(Number size, int type, byte[] uuid) {
        super(size, type);
        this.uuid = uuid;
    }
}
