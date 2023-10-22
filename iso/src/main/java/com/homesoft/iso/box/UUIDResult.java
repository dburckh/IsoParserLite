package com.homesoft.iso.box;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.Type;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class UUIDResult implements Type {
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
    public int getType() {
        return BoxTypes.TYPE_uuid;
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
