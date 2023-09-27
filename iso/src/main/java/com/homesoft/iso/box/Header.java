package com.homesoft.iso.box;

import com.homesoft.iso.DataUtil;
import com.homesoft.iso.Media;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Header {
    private final long creationTime;
    private final long modificationTime;
    private final int timescale;
    final long duration;

    Header(long creationTime, long modificationTime, int timescale, long duration) {
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.timescale = timescale;
        this.duration = duration;
    }

    public long getCreationTime() {
        return Media.toJavaTime(creationTime);
    }

    public long getModificationTime() {
        return Media.toJavaTime(modificationTime);
    }

    public int getTimescale() {
        return timescale;
    }

    public long getDuration() {
        return TimeUnit.SECONDS.toMillis(duration) / DataUtil.getUInt(timescale);
    }

    protected String toStringPrefix() {
        return getClass().getSimpleName() + "{creation="+new Date(getCreationTime()) + ", modification=" + new Date(getModificationTime()) + ", duration=" + getDuration()/1000f;
    }
    public String toString() {
        return toStringPrefix() + "}";
    }
}
