package com.homesoft.iso;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.channels.FileChannel;

public class IsoParser {
    public static final Object[] OBJECT_ARRAY = new Object[0];

    public static void parse(ContainerBox containerBox,
                             StreamReader streamReader,
                             ParseListener parseListener) throws IOException {
        final long end = (streamReader instanceof RandomStreamReader) ? ((RandomStreamReader) streamReader).size() : Long.MAX_VALUE;
        parse(containerBox, streamReader, parseListener, end);
    }

    public static void parse(ContainerBox containerBox,
                             StreamReader streamReader,
                             ParseListener parseListener,
                             long end) throws IOException {
        long start = streamReader.position();
            while (start < end) {
                final BoxHeader boxHeader;
                try {
                    boxHeader = BoxHeader.readBox(streamReader);
                } catch (BufferUnderflowException e) {
                    // This happens when we hit the end of a true stream
                    break;
                }
                final Box box = containerBox.getBox(boxHeader.type);
                if (box != null) {
                    final Object result = box.read(boxHeader, streamReader, box.isFullBox() ? streamReader.getInt() :  0);
                    if (box instanceof ContainerBox) {
                        parseListener.onContainerStart(boxHeader, result);
                        parse((ContainerBox) box, streamReader, parseListener, start + boxHeader.getSize());
                        parseListener.onContainerEnd(boxHeader);
                    } else {
                        parseListener.onParsed(boxHeader, result);
                    }
                    if (parseListener.isCancelled()) {
                        return;
                    }
                }
                if (boxHeader.getSize() == BoxHeader.SIZE_EOF) {
                    break;
                }
                start += boxHeader.getSize();
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
