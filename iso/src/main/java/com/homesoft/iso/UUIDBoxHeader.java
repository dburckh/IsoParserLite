package com.homesoft.iso;

public class UUIDBoxHeader extends BoxHeader {
    private final byte[] uuid;

    UUIDBoxHeader(Number size, int type, byte[] uuid) {
        super(size, type);
        this.uuid = uuid;
    }
}
