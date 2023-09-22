package com.homesoft.iso;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class IsoParser {
    public static final Object[] OBJECT_ARRAY = new Object[0];

    public static Object[] parse(ContainerParser containerParser, StreamReader streamReader) throws IOException {
        final long end = (streamReader instanceof RandomStreamReader) ? ((RandomStreamReader) streamReader).size() : Long.MAX_VALUE;
        return parse(containerParser, streamReader, end);
    }

    public static Object[] parse(ContainerParser containerParser, StreamReader streamReader, long end) throws IOException {
        final ArrayList<Object> list = new ArrayList<>();
        long start = streamReader.position();
            while (start < end) {
                final Box box;
                try {
                    box = Box.readBox(streamReader);
                } catch (BufferUnderflowException e) {
                    // This happens when we hit the end of a true stream
                    break;
                }
                final BoxParser boxParser = containerParser.getParser(box.type);
                if (boxParser != null) {
                    final Object result = boxParser.parse(box, streamReader, boxParser.isFullBox() ? streamReader.getInt() :  0);
                    list.add(result);
                }
                if (box.getSize() == Box.SIZE_EOF) {
                    break;
                }
                start += box.getSize();
                if (streamReader instanceof RandomStreamReader) {
                    ((RandomStreamReader)streamReader).position(start);
                } else {
                    streamReader.skip(start - streamReader.position());
                }
            }
        return list.toArray(OBJECT_ARRAY);

    }

    public static RandomStreamReader newStreamReader(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        final FileChannel fileChannel = randomAccessFile.getChannel();
        return new FileChannelReader(fileChannel, 1024);
    }

}
