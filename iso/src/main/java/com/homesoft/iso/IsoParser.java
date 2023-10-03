package com.homesoft.iso;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.channels.FileChannel;

public abstract class IsoParser<T> {
    private final ContainerBox rootContainerBox;
    protected final ParseListener parseListener;

    private final DependencyManager dependencyManager;

    public static final Object[] OBJECT_ARRAY = new Object[0];

    public static long getEnd(@NonNull StreamReader streamReader) throws IOException {
        return (streamReader instanceof RandomStreamReader) ? ((RandomStreamReader) streamReader).size() : Long.MAX_VALUE;
    }

    public abstract T parse(@NonNull StreamReader streamReader) throws IOException;
    public IsoParser(@NonNull ContainerBox rootContainerBox, @NonNull ParseListener parseListener) {
        this.rootContainerBox = rootContainerBox;
        this.parseListener = parseListener;
        dependencyManager = rootContainerBox instanceof DependencyManager ?
                (DependencyManager) rootContainerBox : DependencyManager.NULL;
    }

    public T parse(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        final FileChannel fileChannel = randomAccessFile.getChannel();
        return parse(new FileChannelReader(fileChannel, 1024));
    }


    protected void parseImpl(@NonNull StreamReader streamReader) throws IOException {
        parseImpl(streamReader, getEnd(streamReader));
    }
    protected void parseImpl(StreamReader streamReader, long end) throws IOException {
        parseImpl(streamReader, end, rootContainerBox);
    }

    /**
     * Parse an ISO BMFF stream
     * @param streamReader input stream for the parser
     * @param end last byte of the file or {@link Long#MAX_VALUE} if unknown
     */
    protected void parseImpl(StreamReader streamReader, long end, ContainerBox containerBox) throws IOException {
        long start = streamReader.position();
            while (start < end) {
                final BoxHeader boxHeader;
                try {
                    boxHeader = BoxHeader.readBox(streamReader);
                } catch (BufferUnderflowException e) {
                    // This happens when we hit the end of a true stream
                    break;
                }
                final int type = boxHeader.type;
                final Box box = containerBox.getBox(type);
                if (box != null) {
                    final Object result = box.read(boxHeader, streamReader, box.isFullBox() ? streamReader.getInt() :  0);
                    dependencyManager.updateDependencies(box, result);
                    if (box instanceof ContainerBox) {
                        parseListener.onContainerStart(type, result);
                        parseImpl(streamReader, start + boxHeader.getSize(), (ContainerBox) box);
                        parseListener.onContainerEnd(type);
                    } else {
                        parseListener.onParsed(type, result);
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
}
