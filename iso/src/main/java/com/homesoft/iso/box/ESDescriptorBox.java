package com.homesoft.iso.box;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * class ES_Descriptor extends BaseDescriptor : bit(8) tag=ES_DescrTag {
 *     bit(16) ES_ID;
 *     bit(1) streamDependenceFlag;
 *     bit(1) URL_Flag;
 *     bit(1) OCRstreamFlag;
 *     bit(5) streamPriority;
 *     if (streamDependenceFlag)
 *         bit(16) dependsOn_ES_ID;
 *     if (URL_Flag) {
 *         bit(8) URLlength;
 *         bit(8) URLstring[URLlength];
 *     }
 *     if (OCRstreamFlag)
 *         bit(16) OCR_ES_Id;
 *     DecoderConfigDescriptor decConfigDescr;
 *     if (ODProfileLevelIndication==0x01) //no SL extension.
 *     {
 *         SLConfigDescriptor slConfigDescr;
 *     }
 *     else // SL extension is possible.
 *     {
 *         SLConfigDescriptor slConfigDescr;
 *     }
 *     IPI_DescrPointer ipiPtr[0 .. 1];
 *     IP_IdentificationDataSet ipIDS[0 .. 255];
 *     IPMP_DescriptorPointer ipmpDescrPtr[0 .. 255];
 *     LanguageDescriptor langDescr[0 .. 255];
 *     QoS_Descriptor qosDescr[0 .. 1];
 *     RegistrationDescriptor regDescr[0 .. 1];
 *     ExtensionDescriptor extDescr[0 .. 255];
 * }
*/
public class ESDescriptorBox implements TypedBox {
    private static final byte ES_DESCR_TAG = 3;
    private static final byte DECODER_CONFIG_DESCR_TAG = 4;
    private static final byte DECODER_SPECIFIC_INFO_TAG = 5;
    private static final int STREAM_DEPENDENCY_MASK = 1<<7;
    private static final int URL_MASK = 1<<6;
    private static final int OCR_STREAM_MASK = 1<<5;

    @Override
    public boolean isFullBox() {
        return true;
    }

    @Nullable
    @Override
    public DecoderConfigDescriptor read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        final ByteBuffer byteBuffer = StreamUtil.requireSharedBuffer(boxHeader.getPayloadSize(isFullBox()), streamReader);
        final byte tag = byteBuffer.get();
        if (tag != ES_DESCR_TAG) {
            return null;
        }
        final int size = readEsSize(byteBuffer);
        StreamUtil.skip(2, byteBuffer); //ES_ID
        final int flags = StreamUtil.getUByte(streamReader);
        if ((flags & STREAM_DEPENDENCY_MASK) > 0) {
            StreamUtil.skip(2, byteBuffer); // dependsOnEsId
        }
        if ((flags & URL_MASK) > 0) {
            final int bytes = StreamUtil.getUByte(byteBuffer);
            StreamUtil.skip(bytes, byteBuffer); // URL
        }
        if ((flags & OCR_STREAM_MASK) > 0) {
            StreamUtil.skip(2, byteBuffer); // oCREsId
        }

        return getDecoderConfigDescriptor(byteBuffer);
    }

    private DecoderConfigDescriptor getDecoderConfigDescriptor(final ByteBuffer byteBuffer) {
        final byte tag = byteBuffer.get();
        if (tag != DECODER_CONFIG_DESCR_TAG) {
            return null;
        }
        final int size = readEsSize(byteBuffer);
        final byte objectTypeIndication = byteBuffer.get();
        final int i = byteBuffer.getInt();
        final int streamType = (i >> 26) & 0x3f; // Top 6 bits
        // bit(1) upStream
        // bit(1) reserved=1
        final int bufferSizeDB = i & 0xffffff;
        return new DecoderConfigDescriptor(
                objectTypeIndication, streamType, bufferSizeDB, byteBuffer.getInt(),
                byteBuffer.getInt(), getDecoderSpecificInfo(byteBuffer));
    }

    private DecoderSpecificInfo getDecoderSpecificInfo(final ByteBuffer byteBuffer) {
        final byte tag = byteBuffer.get();
        if (tag != DECODER_SPECIFIC_INFO_TAG) {
            return null;
        }
        final int size = readEsSize(byteBuffer);
        final byte[] bytes = new byte[size];
        byteBuffer.get(bytes);
        return new DecoderSpecificInfo(
                new CodecSpecificData.TypedConfig(CodecSpecificData.TYPE_NA, ByteBuffer.wrap(bytes)));
    }

    private int readEsSize(ByteBuffer byteBuffer) {
        int size = 0;
        for (int i = 0; i < 4; ++i) {
            final byte b = byteBuffer.get();
            final int lower = b & 0x3f;
            size = (size << 7) + lower;
            // True if msb is not test
            if (b >= 0) {
                break;
            }
        }
        return size;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_esds;
    }
}
