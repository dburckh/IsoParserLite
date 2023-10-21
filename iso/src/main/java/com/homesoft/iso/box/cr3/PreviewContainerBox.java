package com.homesoft.iso.box.cr3;

import androidx.annotation.Nullable;

import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.UUIDBoxHeader;
import com.homesoft.iso.box.BaseContainerBox;
import com.homesoft.iso.box.UUIDResult;

import java.io.IOException;

public class PreviewContainerBox extends BaseContainerBox {
    @Override
    public boolean isFullBox() {
        return true;
    }

    @Nullable
    @Override
    public UUIDResult read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        if (boxHeader instanceof UUIDBoxHeader) {
            return new UUIDResult(((UUIDBoxHeader) boxHeader).uuid, streamReader.getInt());
        }
        return null;
    }
}
