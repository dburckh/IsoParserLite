package com.homesoft.iso.reader;

public class MediaHeader extends Header {

    private final String language;

    public MediaHeader(long creationTime, long modificationTime, int timescale, long duration, short language) {
        super(creationTime, modificationTime, timescale, duration);
        final char[] chars = new char[3];
        for (int i=0;i<3;i++) {
            final int shift = (2 - i) * 5;
            chars[i] = (char)(((language >> shift) & 0x1f) | 0x60);
        }
        this.language = new String(chars);
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return toStringPrefix() + ", lang=" + language + "}";
    }
}
