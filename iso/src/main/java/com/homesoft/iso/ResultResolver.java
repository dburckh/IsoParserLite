package com.homesoft.iso;

import androidx.annotation.Nullable;

import com.homesoft.iso.box.MediaHeaderBox;

import java.util.NoSuchElementException;

/**
 * Used to resolve upstream dependencies for Parsers
 * @see MediaHeaderBox
 */
public interface ResultResolver {
    /**
     * Get the result from the type passed
     * @return the value, can be null
     * @throws NoSuchElementException if the type is not found
     */
    @Nullable
    Object getResult(int type) throws NoSuchElementException;
}
