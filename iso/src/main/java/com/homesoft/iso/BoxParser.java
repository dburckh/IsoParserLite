package com.homesoft.iso;


import java.io.IOException;

/**
 * Marker interface for BoxParsers
 */
public interface BoxParser {
    /**
     * Returns true if this Box is a "FullBox" type.  FullBoxes have an extra int with
     * version and flags.
     * @see Box#getVersion(int)
     * @see Box#getFlags(int)
     */
    boolean isFullBox();

    /**
     * Parse the Box
     * @param box contains generic box metadata
     * @param versionFlags zero unless {@link #isFullBox()}
     */
    Object parse(Box box, StreamReader streamReader, int versionFlags) throws IOException;
}
