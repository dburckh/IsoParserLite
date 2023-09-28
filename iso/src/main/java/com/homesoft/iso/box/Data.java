package com.homesoft.iso.box;

import androidx.annotation.NonNull;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.Type;

/**
 * Output of the DataBox
 */
public class Data implements Type {
    // https://developer.apple.com/documentation/quicktime-file-format/well-known_types
    public static final int UTF_8 = 1;
    public static final int JPEG = 13;
    public static final int PNG = 14;
    public static final int BE_SIGNED = 21;
    public static final int BE_UNSIGNED = 22;
    public static final int BMP = 27;

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
     * See {@link ITunesItemBox} for a list of known tags
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
        return this.getClass().getSimpleName() + "{type=" + BoxHeader.typeToString(type) + ", dataType=" + dataType + ", locale=" + locale + ", value=" + data;
    }
}
