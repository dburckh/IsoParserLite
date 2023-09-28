package com.homesoft.iso;


import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * Class used to manipulate Atoms or Box
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
     * @return the contents of the box.  See individual implementations for specifics.
     */
    @Nullable
    Object read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException;
}
