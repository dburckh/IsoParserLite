package com.homesoft.iso.reader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TrackHeader extends Header {
    /**
     * The duration is unknown
     */
    public static final long DURATION_UNKNOWN = -1L;
    private final int trackId;

    public final Matrix matrix;

    private final float width;
    private final float height;

    public TrackHeader(long creationTime, long modificationTime, int timescale, long duration,
                       int trackId, byte[] matrixBytes, float width, float height) {
        super(creationTime, modificationTime, timescale, duration);
        this.trackId = trackId;
        this.matrix = new Matrix(matrixBytes);
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

    /**
     * Video matrix
     *  a b u
     *  c d v
     *  x y w
     *
     *  a,b,c,d,x,y = fp16.16
     *  u,v,w = fp2.30
     */
    public static class Matrix {
        private final IntBuffer intBuffer;

        public Matrix(byte[] bytes) {
            if (bytes.length != 36) {
                throw new IllegalArgumentException("Matrix must be 36 bytes");
            }
            intBuffer = ByteBuffer.wrap(bytes).asIntBuffer();
        }

        private float getFp16(int index) {
            return intBuffer.get(index) / 65536.0f;
        }

        private float getFp30(int index) {
            return intBuffer.get(index) / 4.0f;
        }

        public float getA() {
            return getFp16(0);
        }

        public float getB() {
            return getFp16(1);
        }

        public float getU() {
            return getFp30(2);
        }
        public float getC() {
            return getFp16(3);
        }

        public float getD() {
            return getFp16(4);
        }

        public float getV() {
            return getFp30(5);
        }
        public float getX() {
            return getFp16(6);
        }

        public float getY() {
            return getFp16(7);
        }

        public float getW() {
            return getFp30(8);
        }

        public double getRotation() {
            float a = getA();
            float b = getB();
            double rotRaid = 0;
            if (a != 0 || b != 0) {
                double r = Math.sqrt(a * a + b * b);
                rotRaid = b > 0 ? Math.acos(a / r) : -Math.acos(a / r);
            } else {
                float c = getC();
                float d = getD();
                if (c != 0 || d != 0) {
                    double s = Math.sqrt(c * c + d * d);
                    rotRaid = Math.PI / 2 - (d > 0 ? Math.acos(-c / s) : -Math.acos(c / s));
                }
            }
            return Math.toDegrees(rotRaid);
        }
    }
}
