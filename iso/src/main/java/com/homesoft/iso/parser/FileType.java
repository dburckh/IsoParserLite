package com.homesoft.iso.parser;

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
}
