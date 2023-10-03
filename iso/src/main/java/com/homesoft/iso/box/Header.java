package com.homesoft.iso.box;

import com.homesoft.iso.StreamUtil;
import com.homesoft.iso.Movie;

import java.text.DateFormat;
import java.text.FieldPosition;
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
        return Movie.toJavaTime(creationTime);
    }

    public long getModificationTime() {
        return Movie.toJavaTime(modificationTime);
    }

    public int getTimescale() {
        return timescale;
    }

    public long getDuration() {
        return TimeUnit.SECONDS.toMillis(duration) / StreamUtil.getUInt(timescale);
    }

    private static String appendDateTime(String prefix, String name, long millis, StringBuffer sb) {
        if (millis != Movie.MS_B4_1904) {
            final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            final FieldPosition fieldPosition = new FieldPosition(0);
            sb.append(prefix);
            sb.append(name);
            dateFormat.format(millis, sb, fieldPosition);
            return ", ";
        }
        return prefix;
    }

    protected String toStringPrefix() {
        final StringBuffer sb = new StringBuffer(getClass().getSimpleName());
        sb.append("{");
        String prefix = "";
        prefix = appendDateTime(prefix, "creation=", getCreationTime(), sb);
        prefix = appendDateTime(prefix, "modification=", getModificationTime(), sb);
        sb.append(prefix);
        sb.append( "duration=" );
        sb.append(getDuration()/1000f);
        return sb.toString();
    }
    public String toString() {
        return toStringPrefix() + "}";
    }
}
