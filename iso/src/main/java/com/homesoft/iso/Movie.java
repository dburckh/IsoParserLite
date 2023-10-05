package com.homesoft.iso;

import androidx.annotation.NonNull;

import com.homesoft.iso.box.AudioSampleEntry;
import com.homesoft.iso.box.AudioSampleEntryBox;
import com.homesoft.iso.box.Av1DecoderConfigBox;
import com.homesoft.iso.box.AvcDecoderConfigBox;
import com.homesoft.iso.box.Data;
import com.homesoft.iso.box.DataBox;
import com.homesoft.iso.box.DecoderConfigDescriptor;
import com.homesoft.iso.box.ESDescriptorBox;
import com.homesoft.iso.box.FileTypeBox;
import com.homesoft.iso.box.BaseContainerBox;
import com.homesoft.iso.box.GPSCoordinates;
import com.homesoft.iso.box.GPSCoordinatesBox;
import com.homesoft.iso.box.HandlerBox;
import com.homesoft.iso.box.Header;
import com.homesoft.iso.box.HevcDecoderConfigBox;
import com.homesoft.iso.box.MediaHeaderBox;
import com.homesoft.iso.box.MovieHeaderBox;
import com.homesoft.iso.box.PixelAspectRatioBox;
import com.homesoft.iso.box.RootContainerBox;
import com.homesoft.iso.box.SampleDescriptionBox;
import com.homesoft.iso.box.SampleEntry;
import com.homesoft.iso.box.TrackHeader;
import com.homesoft.iso.box.TrackHeaderBox;
import com.homesoft.iso.box.VisualSampleEntry;
import com.homesoft.iso.box.VisualSampleEntryBox;
import com.homesoft.iso.listener.CompositeListener;
import com.homesoft.iso.listener.HierarchyListener;
import com.homesoft.iso.listener.IListListener;
import com.homesoft.iso.listener.AnnotationListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Parser for Media (mp4 and m4a)
 */
public class Movie implements BoxTypes {
    public final static long MS_B4_1904;
    public final static ContainerBox ROOT_CONTAINER;

    static {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        calendar.set(1904, Calendar.JANUARY, 1);
        MS_B4_1904 = calendar.getTimeInMillis();

        final RootContainerBox root = new RootContainerBox();
        final MovieHeaderBox movieHeaderBox = new MovieHeaderBox();
        final HandlerBox handlerBox = new HandlerBox();
        final BaseContainerBox setIndexBox = new BaseContainerBox()
                .addParser(TYPE_data, new DataBox(Data.SET_INDEX));

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
                                                                // VIDEO and SOUND are hacks to support generic types
                                                                .addParser(HandlerBox.VIDEO, new VisualSampleEntryBox()
                                                                        .addParser(new HevcDecoderConfigBox())
                                                                        .addParser(new Av1DecoderConfigBox())
                                                                        .addParser(new PixelAspectRatioBox())
                                                                        .addParser(TYPE_avcC, new AvcDecoderConfigBox())
                                                                )
                                                                .addParser(HandlerBox.SOUND, new AudioSampleEntryBox()
                                                                        .addParser(new ESDescriptorBox())
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .addParser(TYPE_udta, new BaseContainerBox()
                                .addParser(TYPE_meta, new BaseContainerBox(true)
                                        .addParser(new HandlerBox())
                                        .addParser(TYPE_ilst, new BaseContainerBox()
                                                .addParser(TYPE_gnre, new BaseContainerBox()
                                                        .addParser(TYPE_data, new DataBox(Data.BE_UNSIGNED))
                                                )
                                                .addParser(TYPE_trkn, setIndexBox)
                                                .addParser(TYPE_disk, setIndexBox)
                                                .addParser(BaseContainerBox.TYPE_DEFAULT, new BaseContainerBox()
                                                        .addParser(new DataBox())
                                                )
                                                .addParser(TYPE__xyz, new BaseContainerBox()
                                                        .addParser(TYPE_data, new GPSCoordinatesBox())
                                                )
                                        )
                                )
                                .addParser(TYPE__xyz, new GPSCoordinatesBox())
                        )
                );

    }

    public static long toJavaTime(long time) {
        return TimeUnit.SECONDS.toMillis(time) + MS_B4_1904;
    }

    /**
     * Convert an int to a float 16.16
     */
    public static float toFloat(int i) {
        return i / 65536.0f;
    }

    public static void main(String[] args) {
        final String path;
        if (args.length == 0) {
            //path = "C:\\Users\\dburc\\Pictures\\heic\\PXL_20230922_013304243.TS.mp4";
            //path = "C:\\Users\\dburc\\Pictures\\heic\\01 We Are Never Ever Getting Back Together.m4a";
            path = "C:\\\\Users\\\\dburc\\\\Pictures\\\\heic\\\\05 I Am A Man Of Constant Sorrow.m4a";
        } else {
            path = args[0];
        }
        final File file = new File(path);
        try {
            //System.out.println(getDumpParser().parse(file));
            Movie movie = getParser().parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static IsoParser<Movie> getParser() {
        final AnnotationListener annotationListener = new AnnotationListener();
        final CompositeListener compositeListener = new CompositeListener(annotationListener);
        final Work work = new Work(annotationListener);
        annotationListener.add(work);
        compositeListener.add(new IListListener(annotationListener));
        return new IsoParser<Movie>(ROOT_CONTAINER, compositeListener) {
            @Override
            public Movie parse(@NonNull StreamReader streamReader) throws IOException {
                parseImpl(streamReader);
                return new Movie(work);
            }
        };
    }

    public static void dump(File file) throws Exception {
        System.out.println(new StringParser(ROOT_CONTAINER, new HierarchyListener(BoxTypes.TYPE_moov)).parse(file));
    }

    private Header movieHeader;

    public Movie(Work work) {
        // TODO: Organize the media and tracks into something useful.
    }

    static class Track {

    }

    private static class TrackInfo {
        final TrackHeader trackHeader;

        public TrackInfo(@NonNull TrackHeader trackHeader) {
            this.trackHeader = trackHeader;
        }
        @TypeResult(BoxTypes.TYPE_hdlr)
        Integer handler;
        @ClassResult({VisualSampleEntry.class, AudioSampleEntry.class})
        SampleEntry sampleEntry;

        @ClassResult
        DecoderConfigDescriptor decoderConfigDescriptor;
    }
    public static class Work {
        private final AnnotationListener annotationListener;
        Work(AnnotationListener annotationListener) {
            this.annotationListener = annotationListener;
        }
        @TypeResult(BoxTypes.TYPE_mvhd)
        Header movieHeader;

        @ClassResult
        GPSCoordinates gpsCoordinates;

        private ArrayList<TrackInfo> trackList = new ArrayList<>();

        private HashMap<Integer, Object> iListMap = new HashMap<>();

        @TypeResult(BoxTypes.TYPE_Anam)
        Data name;

        @ClassResult
        public void setTrackHeader(TrackHeader trackHeader) {
            final TrackInfo trackInfo = new TrackInfo(trackHeader);
            trackList.add(trackInfo);
            annotationListener.add(trackInfo);
        }
    }
}
