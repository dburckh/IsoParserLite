package com.homesoft.iso;

import androidx.annotation.Nullable;

import com.homesoft.iso.box.MediaHeaderBox;

/**
 * Used to resolve upstream dependencies for Parsers
 * @see MediaHeaderBox
 * @see
 */
public interface ResultResolver {
    @Nullable
    Object getResult(int type);
}
