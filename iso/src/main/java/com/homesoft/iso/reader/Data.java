package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.Type;

/**
 * Output of the DataBox
 */
public class Data implements Type {
    // https://developer.apple.com/documentation/quicktime-file-format/well-known_types
    /**
     * UTF-8 String
     */
    public static final int UTF_8 = 1;
    public static final int JPEG = 13;
    public static final int PNG = 14;
    /**
     * Big Endian Signed
     */
    public static final int BE_SIGNED = 21;
    /**
     * Big Endian Unsigned
     */
    public static final int BE_UNSIGNED = 22;
    public static final int BMP = 27;
    /**
     * Arbitrary constant for SetIndex types
     */
    public static final int SET_INDEX = 127;

    /**
     * Type of the surrounding box
     */
    public final int type;
    /**
     * Type of the data element
     */
    public final int dataType;
    public final int locale;

    /**
     * Data contained in the data tag
     */
    @NonNull
    public Object data;

    public Data(int type, int dataType, int locale, @NonNull Object data) {
        this.type = type;
        this.dataType = dataType;
        this.locale = locale;
        this.data = data;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{type=" + Box.typeToString(type) + ", dataType=" + dataType + ", locale=" + locale + ", value=" + data;
    }
}
