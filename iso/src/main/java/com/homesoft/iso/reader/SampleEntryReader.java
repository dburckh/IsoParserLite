package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxReader;
import com.homesoft.iso.StreamReader;

import java.io.IOException;

public class SampleEntryReader extends BaseBoxContainer implements BoxReader {
    @NonNull
    @Override
    public SampleEntry read(Box box, StreamReader streamReader) throws IOException {
        streamReader.skip(6); // reserved
        return new SampleEntry(streamReader.getShort()); // dataReferenceIndex
    }
}
