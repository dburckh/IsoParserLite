package com.homesoft.iso.box;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.StreamUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Can occur directly under udta (Apple) or
 * in an a data box in ilst->©xyz->data box (Android)
 * Under udta it has 4 extra bytes (0x00 12 15 C7)
 * I can't figure out what they are.
 * 0x0012 seems to be the (fixed?) length.
 * 0x15C7 seems to be an unknown constant
 */
public class GPSCoordinatesBox implements Box {
    /**
     * Parse an ISO 6709 String into lat. and long.
     * @return String[] where index 0=lat, 1=long, 2=altitude(optional)
     */
    @NonNull
    public static String[] splitIso6709(String s) throws IllegalArgumentException {
        final ArrayList<String> list = new ArrayList<>(3);
        int index = -1;
        StringBuilder sb = new StringBuilder(9);
        for (int i=0;i<s.length();i++) {
            final char c = s.charAt(i);
            if (c == '+' || c == '-' || c=='/') {
                if (index != -1) {
                    list.add(sb.toString());
                    sb = new StringBuilder(9);
                }
                if (c=='/') {
                    if (list.size() < 2) {
                        throw new IllegalArgumentException("Expected 2 coordinates");
                    }
                    return list.toArray(StreamUtil.EMPTY_STRING_ARRAY);
                }
                index++;
            }
            sb.append(c);
        }
        throw new IllegalArgumentException("No terminator");
    }

    @Override
    public boolean isFullBox() {
        return false;
    }

    /**
     * @return an ISO 6709 String: the format specific format is "±d.d;±d.d/"
     */
    @Nullable
    @Override
    public String read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        int skipBytes;
        if (boxHeader.type == BoxTypes.TYPE__xyz) {
            // skip the unknown header
            skipBytes = 4;
        } else if (boxHeader.type == BoxTypes.TYPE_data) {
            // skip data header
            skipBytes = 8;
        } else {
            throw new UnsupportedOperationException("only support types [data|@xyz]");
        }
        streamReader.skip(skipBytes);
        int payloadSize = ((int)boxHeader.getPayloadSize(isFullBox())) - skipBytes;
        final byte[] bytes = streamReader.getBytes(payloadSize);
        return new String(bytes);
    }
}
