package com.homesoft.iso;

public interface ParseListener {
    void onContainerStart(Box box, Object result);

    /**
     * Called after a Box has been parsed
     * @param box the box that was just parsed
     * @param result the result of {@link BoxParser#parse(Box, StreamReader, int)}
     */
    void onParsed(Box box, Object result);

    /**
     * Called after a Container has been completely parsed
     */
    void onContainerEnd(Box box);
}
