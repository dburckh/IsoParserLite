package com.homesoft.iso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.reader.BaseBoxContainer;
import com.homesoft.iso.reader.ByteArrayReader;
import com.homesoft.iso.reader.CompactSampleSizeReader;
import com.homesoft.iso.reader.Extent;
import com.homesoft.iso.reader.ExtentReader;
import com.homesoft.iso.reader.FileTypeReader;
import com.homesoft.iso.reader.HandlerReader;
import com.homesoft.iso.reader.Header;
import com.homesoft.iso.reader.IntArrayReader;
import com.homesoft.iso.reader.LongArrayReader;
import com.homesoft.iso.reader.MediaHeaderReader;
import com.homesoft.iso.reader.MovieHeaderReader;
import com.homesoft.iso.reader.RootContainerReader;
import com.homesoft.iso.reader.SampleDescriptionReader;
import com.homesoft.iso.reader.SampleEntryReader;
import com.homesoft.iso.reader.SampleSizeReader;
import com.homesoft.iso.reader.StringReader;
import com.homesoft.iso.reader.TrackHeaderReader;
import com.homesoft.iso.reader.UUIDBox;
import com.homesoft.iso.reader.UUIDResult;
import com.homesoft.iso.reader.VisualSampleEntry;
import com.homesoft.iso.reader.cr3.CRawVisualSampleEntry;
import com.homesoft.iso.reader.cr3.CRawVisualSampleEntryReader;
import com.homesoft.iso.reader.cr3.ImageExtent;
import com.homesoft.iso.reader.cr3.PreviewReader;
import com.homesoft.iso.reader.cr3.PreviewContainerReader;
import com.homesoft.iso.reader.cr3.ThumbnailReader;
import com.homesoft.iso.listener.AnnotationListener;
import com.homesoft.iso.listener.CompositeListener;
import com.homesoft.iso.listener.HierarchyListener;
import com.homesoft.iso.listener.TrackListener;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


/**
 * Detailed spec here:
 * <a href="https://github.com/lclevy/canon_cr3">Laurent Clévy - CR3</a>
 */
public class CanonRaw3 implements BoxTypes {
    public static final int TYPE_CMT1 = 0x434D5431; //Exif IFD0
    public static final int TYPE_CMT2 = 0x434D5432; //Exif ExifIFD
    public static final int TYPE_CMT3 = 0x434D5433; //Canon Makernotes
    public static final int TYPE_CMT4 = 0x434D5434; //Exif GPS IFD

    private static final byte[] CANON_UUID = {(byte)0x85, (byte)0xC0, (byte)0xB6, (byte)0x87, (byte)0x82, 0x0F, 0x11, (byte)0xE0, (byte)0x81, 0x11, (byte)0xF4, (byte)0xCE, 0x46, 0x2B, 0x6A, 0x48};

    private static final byte[] XMP_UUID = {(byte)0xBE, (byte)0x7A, (byte)0xCF, (byte)0xCB, (byte)0x97, (byte)0xA9, (byte)0x42, (byte)0xE8, (byte)0x9C, (byte)0x71, (byte)0x99, (byte)0x94, (byte)0x91, (byte)0xE3, (byte)0xAF, (byte)0xAC};
    private static final byte[] PRVW_UUID = {(byte)0xEA, (byte)0xF4, (byte)0x2B, (byte)0x5E, (byte)0x1C, (byte)0x98, (byte)0x4B, (byte)0x88, (byte)0xB9, (byte)0xFB, (byte)0xB7, (byte)0xDC, (byte)0x40, (byte)0x6E, (byte)0x4D, (byte)0x16};
    public final static IsoParser PARSER;

    static {
        final RootContainerReader root = new RootContainerReader();
        final MovieHeaderReader movieHeaderReader = new MovieHeaderReader();
        final HandlerReader handlerReader = new HandlerReader();

        PARSER = new IsoParser(
                root
                        .addParser(new FileTypeReader())
                        .addParser(TYPE_moov, new BaseBoxContainer()
                                .addParser(movieHeaderReader)
                                .addParser(TYPE_trak, new BaseBoxContainer()
                                        .addParser(new TrackHeaderReader(root, movieHeaderReader))
                                        .addParser(TYPE_mdia, new BaseBoxContainer()
                                                .addParser(new MediaHeaderReader())
                                                .addParser(handlerReader)
                                                .addParser(TYPE_minf, new BaseBoxContainer()
                                                        .addParser(TYPE_stbl, new BaseBoxContainer()
                                                                .addParser(TYPE_stsd, new SampleDescriptionReader(root, handlerReader)
                                                                        .addParser(new CRawVisualSampleEntryReader())
                                                                        .addParser(BoxTypes.TYPE_NA, new SampleEntryReader())
                                                                )
                                                                .addParser(new SampleSizeReader())
                                                                .addParser(new CompactSampleSizeReader())
                                                                .addParser(BoxTypes.TYPE_stco, new IntArrayReader())
                                                                .addParser(BoxTypes.TYPE_co64, new LongArrayReader())
                                                        )
                                                )
                                        )
                                )
                                .addParser(CANON_UUID, new BaseBoxContainer()
                                        .addParser(TYPE_CMT1, new ExtentReader())
                                        .addParser(TYPE_CMT2, new ExtentReader())
                                        .addParser(TYPE_CMT3, new ExtentReader())
                                        .addParser(TYPE_CMT4, new ExtentReader())
                                        .addParser(new ThumbnailReader())
                                )

                        )
                        .addParser(PRVW_UUID, new PreviewContainerReader()
                                .addParser(new PreviewReader())
                        )
                        .addParser(XMP_UUID, new UUIDBox(new ByteArrayReader(false)))
        );
    }

    public static void dump(File file) throws Exception {
        System.out.println(PARSER.dump(file));
    }

    public static void main(String[] args) {
        try {
            dump(new File("./iso/src/test/resources/canon.cr3"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CanonRaw3 parse(File file) throws IOException {
        return parse(IsoParser.getFileChannelReader(file));
    }

    public static CanonRaw3 parse(StreamReader streamReader) throws IOException {
        final Work work = new Work();
        final CompositeListener compositeListener = new CompositeListener(work);
        final TrackListener trackListener = new TrackListener(work);
        compositeListener.add(trackListener, trackListener.getType());
        PARSER.parse(streamReader, compositeListener);
        return new CanonRaw3(work);
    }
    private final Work work;

    CanonRaw3(@NonNull Work work) {
        this.work = work;
    }

    public ImageExtent getThumbnail() {
        return work.thumbnail;
    }

    public ImageExtent getPreview() {
        return work.preview;
    }

    public byte[] getXmp() {
        return work.xmp;
    }

    @Nullable
    public static ImageExtent getImageExtent(TrackListener.VideoTrack videoTrack) {
        VisualSampleEntry visualSampleEntry = videoTrack.getVisualSampleEntry();
        if (visualSampleEntry instanceof CRawVisualSampleEntry) {
            CRawVisualSampleEntry cRawVisualSampleEntry = (CRawVisualSampleEntry) visualSampleEntry;
            if (videoTrack.getSampleSizes() != null && videoTrack.getChunkOffsets() != null) {
                return new ImageExtent(cRawVisualSampleEntry.getImageType(),
                        cRawVisualSampleEntry.getRawWidth(),
                        cRawVisualSampleEntry.getRawHeight(),
                        videoTrack.getChunkOffsets().getLong(0),
                        videoTrack.getSampleSizes().getInt(0));

            }
        }
        return null;
    }

    public static short getImageType(TrackListener.VideoTrack videoTrack) {
        VisualSampleEntry visualSampleEntry = videoTrack.getVisualSampleEntry();
        if (visualSampleEntry instanceof CRawVisualSampleEntry) {
            return ((CRawVisualSampleEntry) visualSampleEntry).getImageType();
        }
        return -1;
    }

    public ImageExtent getBestImageTrack(short type) {
        return work.getBestImageTrack(type);
    }

    public ImageExtent getBestJpeg() {
        return ImageExtent.getBestImage(work.preview,
                getBestImageTrack(CRawVisualSampleEntry.IMAGE_TYPE_JPEG));
    }

    public long getCreationTime() {
        return work.movieHeader.getCreationTime();
    }

    public long getModificationTime() {
        return work.movieHeader.getModificationTime();
    }

    public TrackListener.Track getTrack(int index) {
        return work.trackList.get(index);
    }

    public static class Work implements ParseListener {
        public Header movieHeader;

        public ImageExtent thumbnail;

        public byte[] xmp;

        public ImageExtent preview;

        public Extent cmt1;
        public Extent cmt2;
        public Extent cmt3;
        public Extent cmt4;

        public final ArrayList<TrackListener.Track> trackList = new ArrayList<>();

        public ImageExtent getBestImageTrack(short type) {
            ImageExtent imageExtent = null;
            for (TrackListener.Track track : trackList) {
                if (track instanceof TrackListener.VideoTrack) {
                    final TrackListener.VideoTrack videoTrack = (TrackListener.VideoTrack) track;
                    if (getImageType(videoTrack) == type) {
                        final ImageExtent candidate = getImageExtent(videoTrack);
                        if (ImageExtent.COMPARATOR.compare(candidate, imageExtent) > 0) {
                            imageExtent = candidate;
                        }
                    }
                }
            }
            return imageExtent;
        }
        @Override
        public void onContainerStart(int type) {}

        @Override
        public void onParsed(int type, Object result) {
            if (result == null) {
                return;
            }
            switch (type) {
                case TrackListener.TYPE_TRACK:
                    trackList.add((TrackListener.Track)result);
                    break;
                case BoxTypes.TYPE_uuid: {
                    if (result instanceof UUIDResult) {
                        UUIDResult uuidResult = (UUIDResult) result;
                        if (uuidResult.getUuid().equals(ByteBuffer.wrap(CanonRaw3.XMP_UUID))) {
                            xmp = (byte[]) uuidResult.result;
                        }
                    }
                    break;
                }
                case BoxTypes.TYPE_mvhd:
                    movieHeader = (Header) result;
                    break;
                case PreviewReader.TYPE_PRVW:
                    preview = (ImageExtent) result;
                    break;
                case ThumbnailReader.TYPE_THMB:
                    thumbnail = (ImageExtent) result;
                    break;
                case CanonRaw3.TYPE_CMT1:
                    cmt1 = (Extent) result;
                    break;
                case CanonRaw3.TYPE_CMT2:
                    cmt2 = (Extent) result;
                    break;
                case CanonRaw3.TYPE_CMT3:
                    cmt3 = (Extent) result;
                    break;
                case CanonRaw3.TYPE_CMT4:
                    cmt4 = (Extent) result;
                    break;
            }
        }

        @Override
        public void onContainerEnd(int type) {}

        @Override
        public boolean isCancelled() {
            return false;
        }
    }
}
