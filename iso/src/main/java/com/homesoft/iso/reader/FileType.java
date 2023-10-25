package com.homesoft.iso.reader;

import com.homesoft.iso.Box;

import java.util.Arrays;

/**
 * Contents of the ftyp box.  This box is required for all ISOBMFF files.
 */
public class FileType {
    /**
     * The major type of this file, 
     */
    public final int majorBrand;
    public final int minorVersion;
    private final int[] compatibleBrands;

    public FileType(int majorBrand, int minorVersion, int[] compatibleBrands) {
        this.majorBrand = majorBrand;
        this.minorVersion = minorVersion;
        this.compatibleBrands = compatibleBrands;
    }

    public int[] getCompatibleBrands() {
        return Arrays.copyOf(compatibleBrands, compatibleBrands.length);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("{major=");
        sb.append(Box.typeToString(majorBrand));
        sb.append(", minor=");
        sb.append(minorVersion);
        sb.append(", compat=[");
        String div="";
        for (int brand : compatibleBrands) {
            sb.append(div);
            sb.append(Box.typeToString(brand));
            div=",";
        }
        sb.append("]}");
        return sb.toString();
    }
}
