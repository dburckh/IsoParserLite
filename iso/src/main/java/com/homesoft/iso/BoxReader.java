package com.homesoft.iso;


import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * Interface used to read FullBoxes
 */
public interface BoxReader extends BoxParser {
    /**
     * Parse the Box
     * @param box contains generic box metadata
     * @return the contents of the box.  See individual implementations for specifics.
     */
    @Nullable
    Object read(Box box, StreamReader streamReader) throws IOException;
}
