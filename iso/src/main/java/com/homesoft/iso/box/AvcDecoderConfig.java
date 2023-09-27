package com.homesoft.iso.box;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AvcDecoderConfig implements CodecSpecificData {
    public static final byte TYPE_SPS = 7;
    public static final byte TYPE_PPS = 8;
    final private byte configurationVersion;
    final private byte profileIndication;
    final private byte profileCompatibility;
    final private byte levelIndication;
    final private byte nalUnitSize;

    final private byte[][] spsArray;
    final private byte[][] ppsArray;

    public AvcDecoderConfig(byte configurationVersion, byte profileIndication, byte profileCompatibility, byte levelIndication, byte nalUnitSize, byte[][] spsArray, byte[][] ppsArray) {
        this.configurationVersion = configurationVersion;
        this.profileIndication = profileIndication;
        this.profileCompatibility = profileCompatibility;
        this.levelIndication = levelIndication;
        this.nalUnitSize = nalUnitSize;
        this.spsArray = spsArray;
        this.ppsArray = ppsArray;
    }

    public byte getConfigurationVersion() {
        return configurationVersion;
    }

    public byte getProfileIndication() {
        return profileIndication;
    }

    public byte getProfileCompatibility() {
        return profileCompatibility;
    }

    public byte getLevelIndication() {
        return levelIndication;
    }

    public byte getNalUnitSize() {
        return nalUnitSize;
    }

    public byte[][] getSpsArray() {
        return spsArray;
    }

    public byte[][] getPpsArray() {
        return ppsArray;
    }

    private void addData(byte[][] bytes, byte type, List<Data> list) {
        for (byte[] buffer : bytes) {
            list.add(new Data(type, ByteBuffer.wrap(buffer).asReadOnlyBuffer()));
        }
    }

    @Override
    public List<Data> getCodecSpecificData() {
        // Should always be a single SPS and PPS
        final ArrayList<Data> list = new ArrayList<>(2);
        addData(spsArray, TYPE_SPS, list);
        addData(ppsArray, TYPE_PPS, list);
        return list;
    }
}
