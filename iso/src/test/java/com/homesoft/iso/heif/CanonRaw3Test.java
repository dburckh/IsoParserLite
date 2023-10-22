package com.homesoft.iso.heif;

import com.homesoft.iso.CanonRaw3;
import com.homesoft.iso.IsoParser;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class CanonRaw3Test {
    File getRawFile() {
        return new File("src/test/resources/canon.cr3");
    }

    @Test
    public void parseCr3() throws IOException {
        IsoParser<CanonRaw3> parser = CanonRaw3.getParser();
        CanonRaw3 cr3 = parser.parse(getRawFile());
        cr3.getBestPreview();
    }
}
