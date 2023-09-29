package com.homesoft.iso;

import com.homesoft.iso.box.AudioSampleEntryBox;
import com.homesoft.iso.box.Av1DecoderConfigBox;
import com.homesoft.iso.box.AvcDecoderConfigBox;
import com.homesoft.iso.box.Data;
import com.homesoft.iso.box.DataBox;
import com.homesoft.iso.box.ESDescriptorBox;
import com.homesoft.iso.box.FileTypeBox;
import com.homesoft.iso.box.BaseContainerBox;
import com.homesoft.iso.box.HandlerBox;
import com.homesoft.iso.box.HevcDecoderConfigBox;
import com.homesoft.iso.box.MediaHeaderBox;
import com.homesoft.iso.box.MovieHeaderBox;
import com.homesoft.iso.box.PixelAspectRatioBox;
import com.homesoft.iso.box.SampleDescriptionBox;
import com.homesoft.iso.box.TrackHeaderBox;
import com.homesoft.iso.box.VisualSampleEntryBox;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Parser for Media (mp4 and m4a)
 */
public class Media implements BoxTypes {
    public final static long MS_B4_1904;
    static {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.clear();
        calendar.set(1904, Calendar.JANUARY, 1);
        MS_B4_1904 = calendar.getTimeInMillis();
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

    public static ContainerBox getContainerParser(final ResultResolver resultResolver) {
        return new BaseContainerBox()
            .addParser(new FileTypeBox())
            .addParser(TYPE_moov, new BaseContainerBox()
                    .addParser(new MovieHeaderBox())
                    .addParser(TYPE_trak, new BaseContainerBox()
                            .addParser(new TrackHeaderBox(resultResolver))
                            .addParser(TYPE_mdia, new BaseContainerBox()
                                    .addParser(new MediaHeaderBox())
                                    .addParser(new HandlerBox())
                                    .addParser(TYPE_minf, new BaseContainerBox()
                                            .addParser(TYPE_stbl, new BaseContainerBox()
                                                    .addParser(TYPE_stsd, new SampleDescriptionBox(resultResolver)
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
                                            .addParser(TYPE_trkn, new BaseContainerBox()
                                                    .addParser(TYPE_data, new DataBox(Data.SET_INDEX))
                                            )
                                            .addParser(TYPE_disk, new BaseContainerBox()
                                                    .addParser(TYPE_data, new DataBox(Data.SET_INDEX))
                                            )
                                            .addParser(BaseContainerBox.TYPE_DEFAULT, new BaseContainerBox()
                                                    .addParser(new DataBox())
                                            )
                                    )
                            )
                    )
            );
    }

    public static void main(String[] args) {
        //final File file = new File("C:\\Users\\dburc\\Pictures\\heic\\05 I Am A Man Of Constant Sorrow.m4a");
        //final File file = new File("C:\\Users\\dburc\\Pictures\\heic\\VID_20221110_113341.mp4");
        final File file = new File("C:\\Users\\dburc\\Pictures\\heic\\01 We Are Never Ever Getting Back Together.m4a");
        try {
            final Media media = parse(file);
            System.out.println(media);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Media parse(File file) throws Exception {
        try (final StreamReader streamReader = IsoParser.newStreamReader(file)) {
            return parse(streamReader);
        }
    }
    /**
     * Parse a HEIF File using the default HEIF BoxParsers
     */
    public static Media parse(final StreamReader streamReader) throws Exception {
        final HierarchyListener listener = new HierarchyListener(BoxTypes.TYPE_moov);
        IsoParser.parse(getContainerParser(listener), streamReader, listener);
        return new Media(listener);
    }

    private Media(final HierarchyListener listener) {

    }
}
