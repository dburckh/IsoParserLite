package com.homesoft.iso;

import androidx.annotation.Nullable;

public interface ContainerParser extends BoxParser {
    /**
     * Get the {@link BoxParser} for the passed type
     */
    @Nullable
    BoxParser getParser(int type);

}
