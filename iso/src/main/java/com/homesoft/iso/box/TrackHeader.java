package com.homesoft.iso.box;

public class TrackHeader extends Header {
    /**
     * The duration is unknown
     */
    public static final long DURATION_UNKNOWN = -1L;
    private final int trackId;

    private final float width;
    private final float height;

    public TrackHeader(long creationTime, long modificationTime, int timescale, long duration,
                       int trackId,float width, float height) {
        super(creationTime, modificationTime, timescale, duration);
        this.trackId = trackId;
        this.width = width;
        this.height = height;
    }

    /**
     * Get the track duration in millis
     * @return the duration in millis or {@link #DURATION_UNKNOWN}
     */
    @Override
    public long getDuration() {
        if (duration == -1L) {
            return DURATION_UNKNOWN;
        }
        return super.getDuration();
    }

    public int getTrackId() {
        return trackId;
    }

    /**
     * The final display width.
     */
    public float getWidth() {
        return width;
    }

    /**
     * The final display height
     */
    public float getHeight() {
        return height;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(toStringPrefix());
        sb.append(", id=");
        sb.append(trackId);
        if (width != 0f) {
            sb.append(", width=");
            sb.append(width);
        }
        if (height != 0f) {
            sb.append(", height=");
            sb.append(height);
        }
        sb.append("}");
        return sb.toString();
    }
}
