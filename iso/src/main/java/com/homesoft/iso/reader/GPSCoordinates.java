package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.StreamUtil;

import java.util.ArrayList;

public class GPSCoordinates {
    private final String iso6709;
    public GPSCoordinates(String iso6709) {
        this.iso6709 = iso6709;
    }

    /**
     * Parse an ISO 6709 String into lat, long and altitude
     * @return String[] where index 0=lat, 1=long, 2=altitude(optional)
     */
    @NonNull
    public String[] getStringArray() throws IllegalArgumentException {
        final ArrayList<String> list = new ArrayList<>(3);
        int index = -1;
        StringBuilder sb = new StringBuilder(9);
        for (int i=0;i<iso6709.length();i++) {
            final char c = iso6709.charAt(i);
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
    /**
     * Parse an ISO 6709 String into lat, long and altitude
     * @return double[] where index 0=lat, 1=long, 2=altitude(optional)
     */
    public double[] getDoubleArray() {
        final String[] strings = getStringArray();
        final double[] doubles = new double[strings.length];
        for (int i=0;i<strings.length;i++) {
            doubles[i] = Double.parseDouble(strings[i]);
        }
        return doubles;
    }

    @Override
    public String toString() {
        return iso6709;
    }
}
