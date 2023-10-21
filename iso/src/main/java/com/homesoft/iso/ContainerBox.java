package com.homesoft.iso;

import androidx.annotation.Nullable;

public interface ContainerBox extends Box {
    /**
     * Get the {@link Box} for the passed type
     */
    @Nullable
    Box getBox(BoxHeader boxHeader);

}
