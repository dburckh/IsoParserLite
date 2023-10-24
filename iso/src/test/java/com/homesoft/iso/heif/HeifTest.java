package com.homesoft.iso.heif;

import com.homesoft.iso.Heif;
import com.homesoft.iso.box.Av1DecoderConfig;
import com.homesoft.iso.box.HevcDecoderConfig;
import com.homesoft.iso.box.ImageSpatialExtents;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HeifTest {
    File getHeicFile() {
        return new File("src/test/resources/ld.heic");
    }
    File getAvifFile() {
        return new File("src/test/resources/ld.avif");
    }

    @Test
    public void parseHeic() throws IOException  {
        Heif heic = Heif.getParser().parse(getHeicFile());
        Heif.Item item = heic.getPrimaryItem();

        Heif.Grid grid = (Heif.Grid)item;
        ImageSpatialExtents gridIse = grid.getImageSpatialExtents();
        Assert.assertEquals(3072, gridIse.width);
        Assert.assertEquals(4080, gridIse.height);

        List<Heif.Image> imageList = grid.getImageList();
        Assert.assertEquals(48, imageList.size());
        Heif.Image image = imageList.get(0);
        ImageSpatialExtents imageIse = image.getImageSpatialExtents();
        Assert.assertEquals(512, imageIse.width);
        Assert.assertEquals(512, imageIse.height);
        HevcDecoderConfig hvcC = (HevcDecoderConfig)image.getProperty(Heif.TYPE_hvcC);
        Assert.assertEquals(3, hvcC.getCodecSpecificData().size());
    }

    @Test
    public void parseAvif() throws IOException  {
        Heif avif = Heif.getParser().parse(getAvifFile());
        Heif.Item item = avif.getPrimaryItem();

        Heif.Grid grid = (Heif.Grid)item;
        ImageSpatialExtents gridIse = grid.getImageSpatialExtents();
        Assert.assertEquals(3072, gridIse.width);
        Assert.assertEquals(4080, gridIse.height);

        List<Heif.Image> imageList = grid.getImageList();
        Assert.assertEquals(48, imageList.size());
        Heif.Image image = imageList.get(0);
        ImageSpatialExtents imageIse = image.getImageSpatialExtents();
        Assert.assertEquals(512, imageIse.width);
        Assert.assertEquals(512, imageIse.height);
        Av1DecoderConfig imageAv1c = (Av1DecoderConfig)image.getProperty(Heif.TYPE_av1C);
        Assert.assertEquals(1, imageAv1c.getCodecSpecificData().size());
    }
}
