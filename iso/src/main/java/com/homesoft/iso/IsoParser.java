package com.homesoft.iso;

import androidx.annotation.NonNull;

import com.homesoft.iso.listener.HierarchyListener;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class IsoParser {
    private final BoxContainer rootBoxContainer;
    private final DependencyManager dependencyManager;

    public static long getEnd(@NonNull StreamReader streamReader) throws IOException {
        return (streamReader instanceof RandomStreamReader) ? ((RandomStreamReader) streamReader).size() : Long.MAX_VALUE;
    }

    public IsoParser(@NonNull BoxContainer rootBoxContainer) {
        this.rootBoxContainer = rootBoxContainer;
        dependencyManager = rootBoxContainer instanceof DependencyManager ?
                (DependencyManager) rootBoxContainer : DependencyManager.NULL;
    }

    public static FileChannelReader getFileChannelReader(File file) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        final FileChannel fileChannel = randomAccessFile.getChannel();
        return new FileChannelReader(fileChannel, 1024);
    }

    public void parse(File file, ParseListener parseListener) throws IOException {
        parse(getFileChannelReader(file), parseListener);
    }

    public void parse(StreamReader streamReader, ParseListener parseListener) throws IOException {
        new ParseJob(streamReader, parseListener).parse(rootBoxContainer, getEnd(streamReader));
    }

    public String dump(File file) throws IOException {
        final HierarchyListener hierarchyListener = new HierarchyListener();
        final FileChannelReader fileChannelReader = getFileChannelReader(file);
        new ParseJob(getFileChannelReader(file), hierarchyListener).parse(rootBoxContainer, fileChannelReader.size());
        return hierarchyListener.toString();
    }

    private class ParseJob {
        private final HashMap<BoxParser, Object> dependencyValueMap = new HashMap<>();
        private final StreamReader streamReader;
        private final ParseListener parseListener;

        public ParseJob(StreamReader streamReader, ParseListener parseListener) {
            this.streamReader = streamReader;
            this.parseListener = parseListener;
        }

        private void processResult(int type, BoxParser boxParser, Object result) {
            parseListener.onParsed(type, result);
            if (dependencyManager.contains(boxParser)) {
                dependencyValueMap.put(boxParser, result);
            }
        }

        void parse(BoxContainer boxContainer, long end) throws IOException {
            final Object containerDependency;
            if (boxContainer instanceof DependantParser) {
                containerDependency = dependencyValueMap.get(((DependantParser) boxContainer).getDependantParser());
            } else {
                containerDependency = null;
            }
            long start = streamReader.position();
            while (start < end) {
                final Box box;
                try {
                    box = Box.readBox(streamReader);
                } catch (BufferUnderflowException e) {
                    // This happens when we hit the end of a true stream
                    break;
                }
                final int type = box.type;
                final BoxParser parser = boxContainer.getParser(box, containerDependency);
                if (parser != null) {
                    if (parser instanceof BoxReader) {
                        processResult(type, parser, ((BoxReader)parser).read(box, streamReader));
                    } else if (parser instanceof DependantBoxReader) {
                        final Object dependency = dependencyValueMap.get(((DependantParser) parser).getDependantParser());
                        processResult(type, parser,  ((DependantBoxReader) parser).read(box, streamReader, dependency));
                    }
                    if (parser instanceof BoxContainer) {
                        parseListener.onContainerStart(type);
                        parse((BoxContainer) parser, start + box.getSize());
                        parseListener.onContainerEnd(type);
                    }
                    if (parseListener.isCancelled()) {
                        return;
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
    }
}
