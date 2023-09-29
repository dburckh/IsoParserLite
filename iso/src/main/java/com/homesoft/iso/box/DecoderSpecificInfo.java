package com.homesoft.iso.box;

import java.util.Collections;
import java.util.List;

public class DecoderSpecificInfo implements CodecSpecificData {
    private final TypedConfig typedConfig;

    public DecoderSpecificInfo(TypedConfig typedConfig) {
        this.typedConfig = typedConfig;
    }

    @Override
    public List<TypedConfig> getCodecSpecificData() {
        return Collections.singletonList(typedConfig);
    }

    @Override
    public String toString() {
        return "DecoderSpecificInfo{bytes=" + typedConfig.byteBuffer.capacity() + "}";
    }
}
