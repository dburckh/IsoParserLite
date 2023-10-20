package com.homesoft.iso.heif;

import com.homesoft.iso.IsoParser;
import com.homesoft.iso.Movie;
import com.homesoft.iso.box.AudioSampleEntry;
import com.homesoft.iso.box.DecoderConfigDescriptor;
import com.homesoft.iso.box.VisualSampleEntry;
import com.homesoft.iso.listener.TrackListener;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

import javax.swing.plaf.basic.BasicSliderUI;

public class MovieTest {
    private static final int VP09 = 0x76703039;
    File getSongFile() {
        return new File("src/test/resources/song.m4a");
    }
    File getVideoFile() {
        return new File("src/test/resources/video.mp4");
    }

    @Test
    public void parseSong() throws Exception {
        IsoParser<Movie> parser = Movie.getParser();
        Movie movie = parser.parse(getSongFile());

        Movie.MediaMeta mediaMeta = movie.getMediaMeta();
        Assert.assertEquals("I Am A Man Of Constant Sorrow", mediaMeta.getName());
        Assert.assertEquals((Integer) 5, mediaMeta.getTrack());
        List<TrackListener.Track> trackList = movie.getTrackList();
        Assert.assertEquals(1, trackList.size());
        TrackListener.AudioTrack audioTrack = (TrackListener.AudioTrack) trackList.get(0);
        AudioSampleEntry audioSampleEntry = audioTrack.getAudioSampleEntry();

        Assert.assertEquals(2, audioSampleEntry.getChannelCount());
        Assert.assertEquals(44100, audioSampleEntry.getSampleRate());

        DecoderConfigDescriptor decoderConfigDescriptor = audioTrack.getDecoderConfigDescriptor();
        Assert.assertEquals(DecoderConfigDescriptor.OBJECT_TYPE_AAC, decoderConfigDescriptor.getObjectTypeIndication());

        Assert.assertEquals(movie.getDuration(), audioTrack.getDuration());
    }

    @Test
    public void parseVideo() throws Exception {
        IsoParser<Movie> parser = Movie.getParser();
        Movie movie = parser.parse(getVideoFile());

        List<TrackListener.Track> trackList = movie.getTrackList();
        TrackListener.AudioTrack audioTrack = null;
        TrackListener.VideoTrack videoTrack = null;
        for (TrackListener.Track track : trackList) {
            if (track instanceof TrackListener.VideoTrack) {
                videoTrack = (TrackListener.VideoTrack) track;
            } else if (track instanceof TrackListener.AudioTrack) {
                audioTrack = (TrackListener.AudioTrack) track;
            }
        }

        Assert.assertEquals(7918L, movie.getDuration());
        Assert.assertEquals(1695371593000L, movie.getCreationTime());

        DecoderConfigDescriptor decoderConfigDescriptor = audioTrack.getDecoderConfigDescriptor();
        Assert.assertEquals(DecoderConfigDescriptor.OBJECT_TYPE_AAC, decoderConfigDescriptor.getObjectTypeIndication());

        Assert.assertEquals(VP09, videoTrack.getFourCC().intValue());
        Assert.assertEquals(7870L, videoTrack.getDuration());
        // Assert the display size
        Assert.assertEquals(1080.0f, videoTrack.getWidth(),0.1f);
        Assert.assertEquals(1920.0f, videoTrack.getHeight(),0.1f);

        // Assert the encoded size
        VisualSampleEntry visualSampleEntry = videoTrack.getVisualSampleEntry();
        Assert.assertEquals(1080, visualSampleEntry.getWidth());
        Assert.assertEquals(1920, visualSampleEntry.getHeight());

        Assert.assertNotNull(movie.getGpsCoordinates());
    }
}
