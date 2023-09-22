package com.homesoft.iso.parser;

import java.nio.ByteBuffer;

/**
 * Return the CodecConfig in the format Android (OpenMax?) wants
 */
public interface CodecSpecificData {
    ByteBuffer getCSDByteBuffer();
}
