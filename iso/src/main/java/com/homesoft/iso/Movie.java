package com.homesoft.iso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.reader.AudioSampleEntryReader;
import com.homesoft.iso.reader.Av1DecoderConfigReader;
import com.homesoft.iso.reader.AvcDecoderConfigReader;
import com.homesoft.iso.reader.CompactSampleSizeReader;
import com.homesoft.iso.reader.Data;
import com.homesoft.iso.reader.DataReader;
import com.homesoft.iso.reader.ESDescriptorReader;
import com.homesoft.iso.reader.FileTypeReader;
import com.homesoft.iso.reader.BaseBoxContainer;
import com.homesoft.iso.reader.GPSCoordinates;
import com.homesoft.iso.reader.GPSCoordinatesReader;
import com.homesoft.iso.reader.HandlerReader;
import com.homesoft.iso.reader.Header;
import com.homesoft.iso.reader.HevcDecoderConfigReader;
import com.homesoft.iso.reader.IntArrayReader;
import com.homesoft.iso.reader.LongArrayReader;
import com.homesoft.iso.reader.MediaHeaderReader;
import com.homesoft.iso.reader.MovieHeaderReader;
import com.homesoft.iso.reader.PixelAspectRatioReader;
import com.homesoft.iso.reader.RootContainerReader;
import com.homesoft.iso.reader.SampleDescriptionReader;
import com.homesoft.iso.reader.SampleSizeReader;
import com.homesoft.iso.reader.SetIndex;
import com.homesoft.iso.reader.TrackHeaderReader;
import com.homesoft.iso.reader.VisualSampleEntryReader;
import com.homesoft.iso.listener.CompositeListener;
import com.homesoft.iso.listener.HierarchyListener;
import com.homesoft.iso.listener.IListListener;
import com.homesoft.iso.listener.AnnotationListener;
import com.homesoft.iso.listener.TrackListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Parser for Media (mp4 and m4a)
 */
public class Movie implements BoxTypes {
    public final static long MS_B4_1904;
    public final static IsoParser PARSER;

    static {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        calendar.set(1904, Calendar.JANUARY, 1);
        MS_B4_1904 = calendar.getTimeInMillis();

        final RootContainerReader root = new RootContainerReader();
        final MovieHeaderReader movieHeaderReader = new MovieHeaderReader();
        final HandlerReader handlerReader = new HandlerReader();
        final BaseBoxContainer setIndexBox = new BaseBoxContainer()
                .addParser(TYPE_data, new DataReader(Data.SET_INDEX));

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
                                                                .addParser(new SampleSizeReader())
                                                                .addParser(new CompactSampleSizeReader())
                                                                .addParser(BoxTypes.TYPE_stco, new IntArrayReader())
                                                                .addParser(BoxTypes.TYPE_co64, new LongArrayReader())
                                                                .addParser(TYPE_stsd, new SampleDescriptionReader(root, handlerReader)
                                                                        // VIDEO and SOUND are hacks to support generic types
                                                                        .addParser(HandlerReader.VIDEO, new VisualSampleEntryReader()
                                                                                .addParser(new HevcDecoderConfigReader())
                                                                                .addParser(new Av1DecoderConfigReader())
                                                                                .addParser(new PixelAspectRatioReader())
                                                                                .addParser(TYPE_avcC, new AvcDecoderConfigReader())
                                                                        )
                                                                        .addParser(HandlerReader.SOUND, new AudioSampleEntryReader()
                                                                                .addParser(new ESDescriptorReader())
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .addParser(TYPE_udta, new BaseBoxContainer()
                                        .addParser(TYPE_meta, new FullBoxContainer()
                                                .addParser(new HandlerReader())
                                                .addParser(TYPE_ilst, new BaseBoxContainer()
                                                        .addParser(TYPE_gnre, new BaseBoxContainer()
                                                                .addParser(TYPE_data, new DataReader(Data.BE_UNSIGNED))
                                                        )
                                                        .addParser(TYPE_trkn, setIndexBox)
                                                        .addParser(TYPE_disk, setIndexBox)
                                                        .addParser(TYPE_Axyz, new BaseBoxContainer()
                                                                .addParser(TYPE_data, new GPSCoordinatesReader())
                                                        )
                                                        .addParser(BaseBoxContainer.TYPE_DEFAULT, new BaseBoxContainer()
                                                                .addParser(new DataReader())
                                                        )
                                                )
                                        )
                                        .addParser(TYPE_Axyz, new GPSCoordinatesReader())
                                )
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
            path="./iso/src/test/resources/song.m4a";
            System.out.println(new File(path).getAbsolutePath());
            //path = "C:\\Users\\dburc\\Pictures\\heic\\PXL_20230922_013304243.TS.mp4";
            //path = "C:\\Users\\dburc\\Pictures\\heic\\01 We Are Never Ever Getting Back Together.m4a";
            //path = "C:\\\\Users\\\\dburc\\\\Pictures\\\\heic\\\\05 I Am A Man Of Constant Sorrow.m4a";
        } else {
            path = args[0];
        }
        final File file = new File(path);
        try {
            //System.out.println(getDumpParser().parse(file));
            Movie movie = parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Movie parse(File file) throws IOException {
        return parse(IsoParser.getFileChannelReader(file));
    }

    public static Movie parse(StreamReader streamReader) throws IOException {
        final AnnotationListener annotationListener = new AnnotationListener();
        annotationListener.setPoisonType(BoxTypes.TYPE_moov);
        final CompositeListener compositeListener = new CompositeListener(annotationListener);
        final Work work = new Work(annotationListener);
        annotationListener.add(work);
        final IListListener iListListener = new IListListener(annotationListener);
        compositeListener.add(iListListener, iListListener.getType());
        final TrackListener trackListener = new TrackListener(annotationListener);
        compositeListener.add(trackListener, trackListener.getType());

        PARSER.parse(streamReader, compositeListener);
        return new Movie(work);
    }

    public static void dump(File file) throws IOException {
        System.out.println(PARSER.dump(file));
    }

    static String getString(Data data) {
        if (data == null) {
            return null;
        }
        if (data.dataType == Data.UTF_8) {
            return (String) data.data;
        }
        return null;
    }

    private final Work work;
    private final List<TrackListener.Track> trackList;

    public Movie(Work work) {
        this.work = work;
        this.trackList = Collections.unmodifiableList(work.trackList);
    }

    @Nullable
    public MediaMeta getMediaMeta() {
        final MediaMeta mediaMeta = work.mediaMeta;
        if (!mediaMeta.isEmpty()) {
            return mediaMeta;
        }
        return null;
    }

    public long getCreationTime() {
        return work.movieHeader.getCreationTime();
    }

    public long getDuration() {
        return work.movieHeader.getDuration();
    }

    @Nullable
    public GPSCoordinates getGpsCoordinates() {
        if (work.gpsCoordinates != null) {
            return work.gpsCoordinates;
        } else {
            MediaMeta mediaMeta = getMediaMeta();
            if (mediaMeta != null) {
                return mediaMeta.getGpsCoordinates();
            }
        }
        return null;
    }

    public List<TrackListener.Track> getTrackList() {
        return trackList;
    }

    /**
     * Music or Video metadata
     */
    public static class MediaMeta {
        @Nullable
        public String getName() {
            return getString(name);
        }
        @Nullable
        public Integer getTrack() {
            return trackSet == null ? null : trackSet.index;
        }
        @Nullable
        public Integer getTracks() {
            return trackSet == null ? null : trackSet.size;
        }

        @Nullable
        public String getAlbum() {
            return getString(album);
        }

        @Nullable
        public String getAlbumArtist() {
            return getString(albumArist);
        }

        @Nullable
        public GPSCoordinates getGpsCoordinates() {
            return gpsCoordinates;
        }

        @Nullable
        public Integer getYear() {
            String yearString = getString(year);
            if (yearString == null) {
                return null;
            }
            try {
                return Integer.parseInt(yearString);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @TypeResult(BoxTypes.TYPE_Anam)
        Data name;

        @TypeResult(BoxTypes.TYPE_trkn)
        SetIndex trackSet;

        @TypeResult(BoxTypes.TYPE_Aalb)
        Data album;

        @TypeResult(BoxTypes.TYPE_AART)
        Data albumArist;

        @TypeResult(BoxTypes.TYPE_Aday)
        Data year;

        @TypeResult(BoxTypes.TYPE_Axyz)
        GPSCoordinates gpsCoordinates;

        boolean isEmpty() {
            return name == null && trackSet == null && album == null && albumArist == null &&
                    year == null && gpsCoordinates == null;
        }
    }
    public static class Work {
        final MediaMeta mediaMeta = new MediaMeta();
        Work(AnnotationListener annotationListener) {
            annotationListener.add(mediaMeta);
        }
        @TypeResult(BoxTypes.TYPE_mvhd)
        Header movieHeader;

        @ClassResult
        GPSCoordinates gpsCoordinates;

        private final ArrayList<TrackListener.Track> trackList = new ArrayList<>();

        @ClassResult({TrackListener.VideoTrack.class, TrackListener.AudioTrack.class})
        public void setTrackHeader(TrackListener.Track track) {
            trackList.add(track);
        }
    }
}
