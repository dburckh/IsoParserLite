package com.homesoft.iso;

import androidx.annotation.Nullable;

public interface BoxContainer extends BoxParser {
    /**
     * Get the {@link BoxParser} for the passed type
     * @param dependency null unless this is a {@link DependantParser}
     */
    @Nullable
    BoxParser getParser(Box box, Object dependency);
}
