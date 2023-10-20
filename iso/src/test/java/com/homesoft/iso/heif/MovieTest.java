package com.homesoft.iso.heif;

import com.homesoft.iso.IsoParser;
import com.homesoft.iso.Movie;
import com.homesoft.iso.box.AudioSampleEntry;
import com.homesoft.iso.box.DecoderConfigDescriptor;
import com.homesoft.iso.listener.TrackListener;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class MovieTest {
    File getSongFile() {
        return new File("src/test/resources/song.m4a");
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
    }
}
