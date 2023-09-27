package com.homesoft.iso;

public interface ParseListener {
    void onContainerStart(BoxHeader boxHeader, Object result);

    /**
     * Called after a Box has been parsed
     * @param boxHeader the box that was just parsed
     * @param result the result of {@link Box#read(BoxHeader, StreamReader, int)}
     */
    void onParsed(BoxHeader boxHeader, Object result);

    /**
     * Called after a Container has been completely parsed
     */
    void onContainerEnd(BoxHeader boxHeader);

    /**
     * This will cause parsing to stop.
     * Called after {@link #onParsed(BoxHeader, Object)} for {@link Box} or
     * after {@link #onContainerEnd(BoxHeader) for {@link ContainerBox }}
     * @return true to stop parsing, false to continue
     */
    boolean isCancelled();
}
