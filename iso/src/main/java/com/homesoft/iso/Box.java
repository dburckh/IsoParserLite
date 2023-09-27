package com.homesoft.iso;


import java.io.IOException;

/**
 * Marker interface for BoxParsers
 */
public interface Box {
    /**
     * Returns true if this Box is a "FullBox" type.  FullBoxes have an extra int with
     * version and flags.
     * @see BoxHeader#getVersion(int)
     * @see BoxHeader#getFlags(int)
     */
    boolean isFullBox();

    /**
     * Parse the Box
     * @param boxHeader contains generic box metadata
     * @param versionFlags zero unless {@link #isFullBox()}
     */
    Object read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException;
}
