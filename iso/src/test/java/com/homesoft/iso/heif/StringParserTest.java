package com.homesoft.iso.heif;

import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.Movie;
import com.homesoft.iso.StringParser;
import com.homesoft.iso.listener.HierarchyListener;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class StringParserTest {
    @Test
    public void dumpToString() throws IOException {
        final String s = new StringParser(Movie.ROOT_CONTAINER, new HierarchyListener(BoxTypes.TYPE_moov)).parse(MovieTest.getVideoFile());
        Assert.assertTrue(s.indexOf("vp09=VisualSampleEntry{dataReferenceIndex=1, width=1080, height=1920}") > 0);
    }
}
