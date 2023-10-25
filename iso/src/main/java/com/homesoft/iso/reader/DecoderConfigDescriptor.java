package com.homesoft.iso.reader;

import androidx.annotation.Nullable;
/**
 * class DecoderConfigDescriptor extends BaseDescriptor : bit(8)
 * tag=DecoderConfigDescrTag {
 * bit(8) objectTypeIndication;
 * bit(6) streamType;
 * bit(1) upStream;
 * const bit(1) reserved=1;
 * bit(24) bufferSizeDB;
 * bit(32) maxBitrate;
 * bit(32) avgBitrate;
 * DecoderSpecificInfo decSpecificInfo[0 .. 1];
 * profileLevelIndicationIndexDescriptor profileLevelIndicationIndexDescr
 * [0..255];
 * }
 */
public class DecoderConfigDescriptor {
    // https://www.streamcoders.com/products/msdoc/html/T_StreamCoders_Container_MP4_ObjectTypeId.htm
    public static final int OBJECT_TYPE_AAC = 64;
    public static final int OBJECT_TYPE_MP3 = 107;

    private final int objectTypeIndication;
    private final int streamType;
    //upStream
    private final int bufferSizeDB;
    private final int maxBitRate;
    private final int avgBitRate;

    @Nullable
    private final DecoderSpecificInfo decoderSpecificInfo;

    public DecoderConfigDescriptor(int objectTypeIndication, int streamType, int bufferSizeDB,
                                   int maxBitRate, int avgBitRate,
                                   @Nullable DecoderSpecificInfo decoderSpecificInfo) {
        this.objectTypeIndication = objectTypeIndication;
        this.streamType = streamType;
        this.bufferSizeDB = bufferSizeDB;
        this.maxBitRate = maxBitRate;
        this.avgBitRate = avgBitRate;
        this.decoderSpecificInfo = decoderSpecificInfo;
    }

    public int getObjectTypeIndication() {
        return objectTypeIndication;
    }

    public int getStreamType() {
        return streamType;
    }

    public int getBufferSizeDB() {
        return bufferSizeDB;
    }

    public int getMaxBitRate() {
        return maxBitRate;
    }

    public int getAvgBitRate() {
        return avgBitRate;
    }

    @Nullable
    public DecoderSpecificInfo getDecoderSpecificInfo() {
        return decoderSpecificInfo;
    }

    @Override
    public String toString() {
        return "DecoderConfigDescriptor{" +
                "objectTypeIndication=" + objectTypeIndication +
                ", streamType=" + streamType +
                ", bufferSizeDB=" + bufferSizeDB +
                ", maxBitRate=" + maxBitRate +
                ", avgBitRate=" + avgBitRate +
                ", decoderSpecificInfo=" + decoderSpecificInfo +
                '}';
    }
}
