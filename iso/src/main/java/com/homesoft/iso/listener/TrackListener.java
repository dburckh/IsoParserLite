package com.homesoft.iso.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ParseListener;
import com.homesoft.iso.Type;
import com.homesoft.iso.reader.AudioSampleEntry;
import com.homesoft.iso.reader.DecoderConfigDescriptor;
import com.homesoft.iso.reader.HandlerReader;
import com.homesoft.iso.reader.IntArray;
import com.homesoft.iso.reader.LongArray;
import com.homesoft.iso.reader.PixelAspectRatio;
import com.homesoft.iso.reader.SampleEntry;
import com.homesoft.iso.reader.TrackHeader;
import com.homesoft.iso.reader.VisualSampleEntry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Wraps the trck Box hierarchy as a flat Object
 */
public class TrackListener extends ProxyListener implements Type {
    public static final int TYPE_TRACK = BoxTypes.TYPE_trak & Integer.MIN_VALUE;

    private transient TrackHeader trackHeader;
    private transient Integer handler;

    private transient Integer fourCC;
    protected transient SampleEntry sampleEntry;

    private transient IntArray sampleSizes;

    private transient LongArray chunkOffsets;

    private transient DecoderConfigDescriptor decoderConfigDescriptor;

    /**
     * Only valid for video tracks
     */
    protected transient PixelAspectRatio pixelAspectRatio;

    public TrackListener(@NonNull ParseListener parseListener) {
        super(parseListener);
    }

    @Override
    public void onContainerStart(int type) {
    }

    @Override
    public void onParsed(int type, Object result) {
        if (result instanceof TrackHeader) {
            trackHeader = (TrackHeader) result;
        } else if (result instanceof Integer && type == BoxTypes.TYPE_hdlr) {
            handler = (Integer) result;
        } else if (result instanceof DecoderConfigDescriptor) {
            decoderConfigDescriptor = (DecoderConfigDescriptor) result;
        } else if (result instanceof PixelAspectRatio) {
            pixelAspectRatio = (PixelAspectRatio) result;
        } else if (result instanceof SampleEntry) {
            fourCC = type;
            sampleEntry = (SampleEntry) result;
        } else {
            switch (type) {
                case BoxTypes.TYPE_stco:
                case BoxTypes.TYPE_co64:
                    chunkOffsets = (LongArray) result;
                    break;
                case BoxTypes.TYPE_stsz:
                case BoxTypes.TYPE_stz2:
                    sampleSizes = (IntArray) result;
                    break;
            }
        }
    }

    @Override
    public void onContainerEnd(int type) {
        if (type == getType()) {
            Track track;
            if (trackHeader != null && handler != null) {
                if (handler == HandlerReader.SOUND) {
                    track = new AudioTrack(trackHeader, this);
                } else if (handler == HandlerReader.VIDEO) {
                    track = new VideoTrack(trackHeader, this);
                } else {
                    track = new Track(trackHeader, handler, this);
                }
            } else {
                track = null;
            }
            super.onParsed(TYPE_TRACK, track);
            clear();
        }
    }

    private void clear() {
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers())) {
                try {
                    field.set(this, null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_trak;
    }

    public static class Track {
        final TrackHeader trackHeader;
        private final Integer handler;

        private final Integer fourCC;

        protected final SampleEntry sampleEntry;

        private final DecoderConfigDescriptor decoderConfigDescriptor;

        private final LongArray chunkOffsets;

        private final IntArray sampleSizes;

        Track(@NonNull TrackHeader trackHeader, @NonNull Integer handler, TrackListener trackListener) {
            this.trackHeader = trackHeader;
            this.handler = handler;
            this.fourCC = trackListener.fourCC;
            this.sampleEntry = trackListener.sampleEntry;
            this.decoderConfigDescriptor = trackListener.decoderConfigDescriptor;
            this.chunkOffsets = trackListener.chunkOffsets;
            this.sampleSizes = trackListener.sampleSizes;
        }

        public long getDuration() {
            return trackHeader.getDuration();
        }

        public int getId() {
            return trackHeader.getTrackId();
        }

        @Nullable
        public DecoderConfigDescriptor getDecoderConfigDescriptor() {
            return decoderConfigDescriptor;
        }

        @Nullable
        public Integer getFourCC() {
            return fourCC;
        }

        @Nullable
        public LongArray getChunkOffsets() {
            return chunkOffsets;
        }

        @Nullable
        public IntArray getSampleSizes() {
            return sampleSizes;
        }

        public int getHandler() {
            return handler;
        }
    }

    public static class VideoTrack extends Track {
        /**
         * Only valid for video tracks
         */
        protected PixelAspectRatio pixelAspectRatio;

        VideoTrack(@NonNull TrackHeader trackHeader, TrackListener trackListener) {
            super(trackHeader, HandlerReader.VIDEO, trackListener);
            this.pixelAspectRatio = trackListener.pixelAspectRatio;
        }

        public float getWidth() {
            return trackHeader.getWidth();
        }

        public float getHeight() {
            return trackHeader.getHeight();
        }

        @Nullable
        public VisualSampleEntry getVisualSampleEntry() {
            if (sampleEntry instanceof VisualSampleEntry) {
                return (VisualSampleEntry) sampleEntry;
            }
            return null;
        }

        @Nullable
        public PixelAspectRatio getPixelAspectRatio() {
            return pixelAspectRatio;
        }

    }
    public static class AudioTrack extends Track {
        AudioTrack(@NonNull TrackHeader trackHeader, TrackListener trackListener) {
            super(trackHeader, HandlerReader.SOUND, trackListener);
        }

        @Nullable
        public AudioSampleEntry getAudioSampleEntry() {
            if (sampleEntry instanceof AudioSampleEntry) {
                return (AudioSampleEntry) sampleEntry;
            }
            return null;
        }
    }
}
