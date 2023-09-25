package com.homesoft.iso;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class IsoParser {
    public static final Object[] OBJECT_ARRAY = new Object[0];

    public static void parse(ContainerParser containerParser,
                                 StreamReader streamReader,
                                 ParseListener parseListener) throws IOException {
        final long end = (streamReader instanceof RandomStreamReader) ? ((RandomStreamReader) streamReader).size() : Long.MAX_VALUE;
        parse(containerParser, streamReader, parseListener, end);
    }

    public static void parse(ContainerParser containerParser,
                                 StreamReader streamReader,
                                 ParseListener parseListener,
                                 long end) throws IOException {
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
                    if (boxParser instanceof ContainerParser) {
                        parseListener.onContainerStart(box, result);
                        parse((ContainerParser) boxParser, streamReader, parseListener, start + box.getSize());
                        parseListener.onContainerEnd(box);
                    } else {
                        parseListener.onParsed(box, result);
                    }
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

    }

    public static RandomStreamReader newStreamReader(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        final FileChannel fileChannel = randomAccessFile.getChannel();
        return new FileChannelReader(fileChannel, 1024);
    }

}
