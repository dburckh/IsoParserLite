package com.homesoft.iso;

import androidx.annotation.NonNull;

import com.homesoft.iso.box.BaseContainerBox;
import com.homesoft.iso.box.CompactSampleSizeBox;
import com.homesoft.iso.box.ExtentBox;
import com.homesoft.iso.box.FileTypeBox;
import com.homesoft.iso.box.HandlerBox;
import com.homesoft.iso.box.Header;
import com.homesoft.iso.box.IntArrayBox;
import com.homesoft.iso.box.LongArrayBox;
import com.homesoft.iso.box.MediaHeaderBox;
import com.homesoft.iso.box.MovieHeaderBox;
import com.homesoft.iso.box.RootContainerBox;
import com.homesoft.iso.box.SampleDescriptionBox;
import com.homesoft.iso.box.SampleSizeBox;
import com.homesoft.iso.box.StringBox;
import com.homesoft.iso.box.TrackHeaderBox;
import com.homesoft.iso.box.UUIDBox;
import com.homesoft.iso.box.UUIDResult;
import com.homesoft.iso.box.VisualSampleEntry;
import com.homesoft.iso.box.cr3.CRawVisualSampleEntry;
import com.homesoft.iso.box.cr3.CRawVisualSampleEntryBox;
import com.homesoft.iso.box.cr3.JpegImage;
import com.homesoft.iso.box.cr3.PreviewBox;
import com.homesoft.iso.box.cr3.PreviewContainerBox;
import com.homesoft.iso.box.cr3.ThumbnailBox;
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
    public final static ContainerBox ROOT_CONTAINER;

    static {
        final RootContainerBox root = new RootContainerBox();
        final MovieHeaderBox movieHeaderBox = new MovieHeaderBox();
        final HandlerBox handlerBox = new HandlerBox();

        ROOT_CONTAINER = root
                .addParser(new FileTypeBox())
                .addParser(TYPE_moov, new BaseContainerBox()
                        .addParser(movieHeaderBox)
                        .addParser(TYPE_trak, new BaseContainerBox()
                                .addParser(new TrackHeaderBox(root, movieHeaderBox))
                                .addParser(TYPE_mdia, new BaseContainerBox()
                                        .addParser(new MediaHeaderBox())
                                        .addParser(handlerBox)
                                        .addParser(TYPE_minf, new BaseContainerBox()
                                                .addParser(TYPE_stbl, new BaseContainerBox()
                                                        .addParser(TYPE_stsd, new SampleDescriptionBox(root, handlerBox)
                                                                .addParser(new CRawVisualSampleEntryBox())
                                                        )
                                                        .addParser(new SampleSizeBox())
                                                        .addParser(new CompactSampleSizeBox())
                                                        .addParser(BoxTypes.TYPE_stco, new IntArrayBox(true))
                                                        .addParser(BoxTypes.TYPE_co64, new LongArrayBox(true))
                                                )
                                        )
                                )
                        )
                        .addParser(CANON_UUID, new BaseContainerBox()
                                .addParser(TYPE_CMT1, new ExtentBox())
                                .addParser(TYPE_CMT2, new ExtentBox())
                                .addParser(TYPE_CMT3, new ExtentBox())
                                .addParser(TYPE_CMT4, new ExtentBox())
                                .addParser(new ThumbnailBox())
                        )

                )
                .addParser(PRVW_UUID, new PreviewContainerBox()
                        .addParser(new PreviewBox())
                )
                .addParser(XMP_UUID, new UUIDBox(new StringBox(false)));
    }

    public static void dump(File file) throws Exception {
        System.out.println(new StringParser(ROOT_CONTAINER, new HierarchyListener()).parse(file));
    }

    public static void main(String[] args) {
        try {
            dump(new File("./iso/src/test/resources/canon.cr3"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static IsoParser<CanonRaw3> getParser() {
        final AnnotationListener annotationListener = new AnnotationListener();
        final CompositeListener compositeListener = new CompositeListener(annotationListener);
        final Work work = new Work();
        annotationListener.add(work);
        compositeListener.add(new TrackListener(annotationListener));
        return new IsoParser<CanonRaw3>(ROOT_CONTAINER, compositeListener) {
            @Override
            public CanonRaw3 parse(@NonNull StreamReader streamReader) throws IOException {
                parseImpl(streamReader);
                return new CanonRaw3(work);
            }
        };
    }
    private final Work work;

    CanonRaw3(@NonNull Work work) {
        this.work = work;
    }

    public JpegImage getThumbnail() {
        return work.thumbnail;
    }

    public String getXmp() {
        return work.xmp;
    }

    public JpegImage getTrackImage() {
        for (TrackListener.Track track : work.trackList) {
            if (track instanceof TrackListener.VideoTrack) {
                TrackListener.VideoTrack videoTrack = (TrackListener.VideoTrack) track;
                VisualSampleEntry visualSampleEntry = videoTrack.getVisualSampleEntry();
                if (visualSampleEntry instanceof CRawVisualSampleEntry) {
                    CRawVisualSampleEntry cRawVisualSampleEntry = (CRawVisualSampleEntry) visualSampleEntry;
                    if (cRawVisualSampleEntry.getImageType() == CRawVisualSampleEntry.IMAGE_TYPE_JPEG &&
                            track.getSampleSizes() != null && track.getChunkOffsets() != null) {
                        return new JpegImage(cRawVisualSampleEntry.getRawWidth(),
                                cRawVisualSampleEntry.getRawHeight(),
                                track.getChunkOffsets().getLong(0),
                                track.getSampleSizes().getInt(0));

                    }
                }
            }
        }
        return null;
    }

    public JpegImage getBestPreview() {
        JpegImage previewImage = work.preview;
        JpegImage trackImage = getTrackImage();
        if (JpegImage.COMPARATOR.compare(previewImage, trackImage) < 0) {
            return previewImage;
        } else {
            return trackImage;
        }
    }

    public static class Work {
        @TypeResult(BoxTypes.TYPE_mvhd)
        Header movieHeader;

        @TypeResult(ThumbnailBox.TYPE_THMB)
        JpegImage thumbnail;

        String xmp;

        @TypeResult(PreviewBox.TYPE_PRVW)
        JpegImage preview;

        private final ArrayList<TrackListener.Track> trackList = new ArrayList<>();

        @ClassResult({TrackListener.VideoTrack.class, TrackListener.AudioTrack.class})
        public void setTrackHeader(TrackListener.Track track) {
            trackList.add(track);
        }

        @TypeResult(BoxTypes.TYPE_uuid)
        public void setUuidResult(UUIDResult uuidResult) {
            if (uuidResult == null) {
                return;
            }
            if (uuidResult.getUuid().equals(ByteBuffer.wrap(CanonRaw3.XMP_UUID))) {
                xmp = (String)uuidResult.result;
            }
        }
    }
}
