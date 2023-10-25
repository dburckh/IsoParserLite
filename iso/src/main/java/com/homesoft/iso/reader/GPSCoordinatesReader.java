package com.homesoft.iso.reader;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxReader;
import com.homesoft.iso.Box;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

/**
 * Can occur directly under udta (Apple) or
 * in an a data box in ilst->©xyz->data box (Android)
 * Under udta it has 4 extra bytes (0x00 12 15 C7)
 * I can't figure out what they are.
 * 0x0012 seems to be the (fixed?) length.
 * 0x15C7 seems to be an unknown constant
 */
public class GPSCoordinatesReader implements BoxReader {

    /**
     * @return an ISO 6709 String: the format specific format is "±d.d;±d.d/"
     */
    @Nullable
    @Override
    public GPSCoordinates read(Box box, StreamReader streamReader) throws IOException {
        int skipBytes;
        if (box.type == BoxTypes.TYPE_Axyz) {
            // skip the unknown header
            skipBytes = 4;
        } else if (box.type == BoxTypes.TYPE_data) {
            // skip data header
            skipBytes = 8;
        } else {
            throw new UnsupportedOperationException("only support types [data|@xyz]");
        }
        streamReader.skip(skipBytes);
        int payloadSize = ((int) box.getPayloadSize(false)) - skipBytes;
        final byte[] bytes = streamReader.getBytes(payloadSize);
        return new GPSCoordinates(new String(bytes));
    }
}
