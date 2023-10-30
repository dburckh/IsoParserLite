package com.homesoft.iso.reader;

import com.homesoft.iso.StreamUtil;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class UUIDResult {
    private final ByteBuffer uuid;

    public final Object result;

    public UUIDResult(ByteBuffer uuid, Object result) {
        this.uuid = (uuid.isReadOnly() ? uuid : uuid.asReadOnlyBuffer()).duplicate();
        this.uuid.clear();
        this.result = result;
    }

    public ByteBuffer getUuid() {
        return uuid.duplicate();
    }

    @Override
    public String toString() {
        ByteBuffer clone = getUuid();
        byte[] bytes = new byte[clone.capacity()];
        clone.get(bytes);
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("{uuid=");
        sb.append(new BigInteger(1, bytes).toString(16));
        if (result != null) {
            sb.append(", result");
            StreamUtil.append(sb, result);
        }
        sb.append('}');
        return sb.toString();
    }
}
