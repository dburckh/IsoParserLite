package com.homesoft.iso;

public interface ParseListener {
    void onContainerStart(int type, Object result);

    /**
     * Called after a Box has been parsed
     * @param type the box that was just parsed
     * @param result the result of {@link Box#read(BoxHeader, StreamReader, int)}
     */
    void onParsed(int type, Object result);

    /**
     * Called after a Container has been completely parsed
     */
    void onContainerEnd(int type);

    /**
     * This will cause parsing to stop.
     * Called after {@link #onParsed(int, Object)} for {@link Box} or
     * after {@link #onContainerEnd(int) for {@link ContainerBox }}
     * @return true to stop parsing, false to continue
     */
    boolean isCancelled();
}
