package com.homesoft.iso.heif;

import com.homesoft.iso.CanonRaw3;
import com.homesoft.iso.IsoParser;
import com.homesoft.iso.box.HandlerBox;
import com.homesoft.iso.box.cr3.CRawVisualSampleEntry;
import com.homesoft.iso.box.cr3.ImageExtent;
import com.homesoft.iso.listener.TrackListener;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class CanonRaw3Test {
    private static final String XMP_NAMESPACE = "xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"";
    File getRawFile() {
        return new File("src/test/resources/canon.cr3");
    }

    @Test
    public void parseCr3() throws IOException {
        IsoParser<CanonRaw3> parser = CanonRaw3.getParser();
        CanonRaw3 cr3 = parser.parse(getRawFile());

        ImageExtent bestRaw = cr3.getBestImageTrack(CRawVisualSampleEntry.IMAGE_TYPE_RAW);
        Assert.assertEquals(6288, bestRaw.getWidth());
        Assert.assertEquals(4056, bestRaw.getHeight());

        ImageExtent preview = cr3.getPreview();
        assertJpegImage(preview);

        ImageExtent bestJpeg = cr3.getBestJpeg();
        Assert.assertEquals(CRawVisualSampleEntry.IMAGE_TYPE_JPEG, bestJpeg.getType());
        Assert.assertNotEquals(bestJpeg, preview);
        Assert.assertEquals(6000, bestJpeg.getWidth());
        Assert.assertEquals(4000, bestJpeg.getHeight());

        TrackListener.Track track = cr3.getTrack(3);
        Assert.assertEquals(HandlerBox.META, track.getHandler());

        Assert.assertTrue(cr3.getXmp().indexOf(XMP_NAMESPACE) >= 0);

        Assert.assertEquals(1522460279000L, cr3.getCreationTime());
        Assert.assertEquals(1522460279000L, cr3.getModificationTime());

        ImageExtent thumbnail = cr3.getThumbnail();

        assertJpegImage(thumbnail);
        Assert.assertEquals(CRawVisualSampleEntry.IMAGE_TYPE_JPEG, thumbnail.getType());
        Assert.assertEquals(160, thumbnail.getWidth());
    }

    private void assertJpegImage(ImageExtent imageExtent) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(getRawFile(), "r")) {
            randomAccessFile.seek(imageExtent.offset);
            // Jpeg starts with 0xFFD8
            Assert.assertEquals(0xff, randomAccessFile.read());
            Assert.assertEquals(0xd8, randomAccessFile.read());

            randomAccessFile.seek(imageExtent.offset + imageExtent.size - 2);
            // Jpeg end with 0xFFD9
            Assert.assertEquals(0xff, randomAccessFile.read());
            Assert.assertEquals(0xd9, randomAccessFile.read());
        }
    }
}
