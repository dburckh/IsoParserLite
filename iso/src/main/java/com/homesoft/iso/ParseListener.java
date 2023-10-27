package com.homesoft.iso;

public interface ParseListener {
    void onContainerStart(int type);

    /**
     * Called after a Box has been parsed
     * @param type the box that was just parsed
     * @param result the result of {@link BoxReader#read(Box, StreamReader)}
     */
    void onParsed(int type, Object result);

    /**
     * Called after a Container has been completely parsed
     */
    void onContainerEnd(int type);

    /**
     * This will cause parsing to stop.
     * Called after {@link #onParsed(int, Object)} for {@link BoxReader} or
     * after {@link #onContainerEnd(int) for {@link BoxContainer }}
     * @return true to stop parsing, false to continue
     */
    boolean isCancelled();

    ParseListener NULL = new ParseListener() {
        @Override
        public void onContainerStart(int type) {}

        @Override
        public void onParsed(int type, Object result) {}

        @Override
        public void onContainerEnd(int type) {}

        @Override
        public boolean isCancelled() {
            return true;
        }
    };
}
