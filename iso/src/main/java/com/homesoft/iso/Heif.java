package com.homesoft.iso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.reader.Av1DecoderConfigReader;
import com.homesoft.iso.reader.CodecSpecificData;
import com.homesoft.iso.reader.BaseBoxContainer;
import com.homesoft.iso.reader.ExtentReader;
import com.homesoft.iso.reader.FileTypeReader;
import com.homesoft.iso.reader.HevcDecoderConfigReader;
import com.homesoft.iso.reader.ImageSpatialExtents;
import com.homesoft.iso.reader.ImageSpatialExtentsReader;
import com.homesoft.iso.reader.ItemInfoEntry;
import com.homesoft.iso.reader.ItemInfoReader;
import com.homesoft.iso.reader.ItemLocation;
import com.homesoft.iso.reader.ItemLocationReader;
import com.homesoft.iso.reader.ItemPropertyAssociation;
import com.homesoft.iso.reader.ItemPropertyAssociationReader;
import com.homesoft.iso.reader.ItemReferenceReader;
import com.homesoft.iso.reader.PrimaryItemParser;
import com.homesoft.iso.reader.RootContainerReader;
import com.homesoft.iso.reader.SingleItemTypeReference;
import com.homesoft.iso.reader.StringReader;
import com.homesoft.iso.listener.CompositeListener;
import com.homesoft.iso.listener.ListListener;
import com.homesoft.iso.listener.AnnotationListener;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Parser for HEIF based ISOBMFF files
 * This includes HEIC (HEVC) and AVIF (AV1) image files
 */
public class Heif implements BoxTypes {
    private static final int TYPE_ipco_list = TYPE_ipco | Integer.MIN_VALUE;
    private static final int TYPE_iref_list = TYPE_iref | Integer.MIN_VALUE;
    private static final int TYPE_iinf_list = TYPE_iinf | Integer.MIN_VALUE;
    private static final Object[] EMPTY_ARRAY = new Object[0];

    private static final RootContainerReader ROOT = new RootContainerReader();

    public static final IsoParser PARSER = new IsoParser(
            ROOT
                    .addParser(new FileTypeReader())
                    .addParser(TYPE_meta, new FullBoxContainer()
                            .addParser(TYPE_pitm, new PrimaryItemParser())
                            .addParser(TYPE_iinf, new ItemInfoReader())
                            .addParser(new ItemLocationReader())
                            .addParser(TYPE_iref, new ItemReferenceReader(ROOT))
                            .addParser(TYPE_irpr, new BaseBoxContainer()
                                    .addParser(new ItemPropertyAssociationReader())
                                    .addParser(TYPE_ipco, new BaseBoxContainer()
                                            .addParser(new ImageSpatialExtentsReader())
                                            .addParser(new HevcDecoderConfigReader())
                                            .addParser(new Av1DecoderConfigReader())
                                            .addParser(TYPE_AUXC, new StringReader(true))
                                            .addParser(BaseBoxContainer.TYPE_DEFAULT, new ExtentReader())
                                    )
                            )
                    )
    );

    public static Heif parse(File file) throws IOException {
        return parse(IsoParser.getFileChannelReader(file));
    }

    public static Heif parse(StreamReader streamReader) throws IOException {
        final Work work = new Work();
        final AnnotationListener annotationListener = new AnnotationListener(work);
        final CompositeListener compositeListener = new CompositeListener(annotationListener);
        compositeListener.add(new ListListener(annotationListener, TYPE_iinf, TYPE_iinf_list));
        compositeListener.add(new ListListener(annotationListener, TYPE_ipco, TYPE_ipco_list));
        compositeListener.add(new ListListener(annotationListener, TYPE_iref, TYPE_iref_list));

        PARSER.parse(streamReader, compositeListener);
        return new Heif(work);
    }

    public static void main(String[] args) {
        final File file = new File("C:\\Users\\dburc\\Pictures\\heic\\20230907_162647.heic");
        try {
            final Heif heif = parse(file);
            System.out.println(heif);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dump(File file) throws Exception {
        System.out.println(PARSER.dump(file));
    }

    private final int primaryItemId;
    @NonNull
    private final ItemInfoEntry[] itemInfoEntries;
    @NonNull
    private final ItemPropertyAssociation[] itemPropertyAssociations;
    @NonNull
    private final Object[] propertyArray;

    @NonNull
    private final SingleItemTypeReference[] itemReferences;

    @NonNull
    private final ItemLocation[] itemLocations;

    /**
     * Private constructor validates the output of parse
     */
    private Heif(final Work work) {
        primaryItemId = Objects.requireNonNull(work.primaryItemType);
        itemInfoEntries = StreamUtil.toArray(Objects.requireNonNull(work.itemInfoEntryList), ItemInfoEntry.class);
        itemPropertyAssociations = Objects.requireNonNull(work.itemPropertyAssociations);
        propertyArray = StreamUtil.toArray(Objects.requireNonNull(work.properyList), Object.class);
        itemReferences = work.itemReferenceList == null ? new SingleItemTypeReference[0] :
                StreamUtil.toArray(work.itemReferenceList, SingleItemTypeReference.class);
        itemLocations = Objects.requireNonNull(work.itemLocations);
    }

    private Object[] getProperties(int id) {
        final ItemPropertyAssociation itemPropertyAssociation = StreamUtil.findId(id, itemPropertyAssociations);
        if (itemPropertyAssociation == null) {
            return EMPTY_ARRAY;
        }
        final int[] propertyIds = itemPropertyAssociation.getAssociations();
        if (propertyIds.length == 0 || propertyIds[0] == 0) {
            return EMPTY_ARRAY;
        }
        final Object[] properties = new Object[propertyIds.length];
        for (int i=0;i<properties.length;i++) {
            properties[i] = propertyArray[propertyIds[i] - 1];
        }
        return properties;
    }

    /**
     * Get an Image based on it's ID
     */
    @Nullable
    public Image getImage(int id) {
        final ItemInfoEntry itemInfoEntry = StreamUtil.findId(id, itemInfoEntries);
        if (itemInfoEntry == null) {
            return null;
        }
        return getImage(itemInfoEntry);
    }

    /**
     * Get an Image based on it's ItemInfoEntry
     */
    @Nullable
    Image getImage(@NonNull final ItemInfoEntry itemInfoEntry) {
        final ItemLocation itemLocation = StreamUtil.findId(itemInfoEntry.id, itemLocations);
        if (itemLocation == null) {
            return null;
        }
        final Object[] properties = getProperties(itemInfoEntry.id);
        return new Image(itemInfoEntry, properties, itemLocation);
    }

    public int getPrimaryItemId() {
        return primaryItemId;
    }

    @NonNull
    public Item getPrimaryItem() throws InvalidObjectException {
        final ItemInfoEntry itemInfoEntry = StreamUtil.findId(primaryItemId, itemInfoEntries);
        if (itemInfoEntry == null) {
            throw new InvalidObjectException("Primary Item Info not found in ItemInfoEntries");
        }
        final Item item;
        switch (itemInfoEntry.type) {
            case ItemInfoEntry.ITEM_TYPE_grid:
                item = getGrid(itemInfoEntry);
                break;
            case ItemInfoEntry.ITEM_TYPE_hvc1:
            case ItemInfoEntry.ITEM_TYPE_av01:
                item = getImage(itemInfoEntry);
                break;
            default:
                item = null;
        }
        if (item == null) {
            throw new InvalidObjectException("Primary Item could not be created.");
        }
        return item;
    }

    private Grid getGrid(final ItemInfoEntry itemInfoEntry) {
        final Object[] properties = getProperties(itemInfoEntry.id);
        List<Image> imageList = Collections.emptyList();
        for (SingleItemTypeReference reference : itemReferences) {
            if (reference.type == ItemReferenceReader.TYPE_DIMG && reference.fromId == itemInfoEntry.id) {
                final int[] imageIds = reference.getToIds();
                imageList = new ArrayList<>(imageIds.length);
                for (int imageId : imageIds) {
                    final Image image = getImage(imageId);
                    if (image != null) {
                        imageList.add(image);
                    }
                }
            }
        }
        return new Grid(itemInfoEntry, properties, imageList);
    }

    private static class Work {
        @TypeResult(BoxTypes.TYPE_pitm)
        Integer primaryItemType;
        @TypeResult(BoxTypes.TYPE_ipma)
        ItemPropertyAssociation[] itemPropertyAssociations;
        @TypeResult(TYPE_iinf_list)
        List<ItemInfoEntry> itemInfoEntryList;
        @TypeResult(TYPE_ipco_list)
        List<Object> properyList;
        @TypeResult(TYPE_iref_list)
        List<SingleItemTypeReference> itemReferenceList;
        @TypeResult(BoxTypes.TYPE_iloc)
        ItemLocation[] itemLocations;
    }

    public static class Item implements Type {
        final ItemInfoEntry itemInfoEntry;
        final Object[] properties;

        Item(ItemInfoEntry itemInfoEntry, Object[] properties) {
            this.itemInfoEntry = itemInfoEntry;
            this.properties = properties;
        }

        @Override
        public int getType() {
            return itemInfoEntry.type;
        }

        public Type getProperty(int type) {
            return StreamUtil.findType(type, properties);
        }

        /**
         * Get the <code>ImageSpatialExtents</code> aka dimensions for this image
         */
        public ImageSpatialExtents getImageSpatialExtents() {
            Object data = getProperty(TYPE_ispe);
            if (data instanceof ImageSpatialExtents) {
                return (ImageSpatialExtents) data;
            } else {
                return null;
            }
        }
    }

    /**
     * grid wrapper
     * A grid is an image composed of other images
     */
    public static class Grid extends Item {
        private final List<Image> imageList;

        Grid(ItemInfoEntry itemInfoEntry, Object[] properties, List<Image> imageList) {
            super(itemInfoEntry, properties);
            this.imageList = imageList;
        }

        public List<Image> getImageList() {
            return Collections.unmodifiableList(imageList);
        }
    }

    /**
     * An Image within this file.
     * This maybe a subImage contained in a {@link Grid} or standalone Image
     */
    public static class Image extends Item {
        private final ItemLocation itemLocation;

        Image(@NonNull ItemInfoEntry itemInfoEntry, @NonNull Object[] properties,
              @NonNull ItemLocation itemLocation) {
            super(itemInfoEntry, properties);
            this.itemLocation = itemLocation;
        }

        /**
         * Get the {@link CodecSpecificData} for this Image
         */
        public CodecSpecificData getCodecSpecificData() {
            return StreamUtil.findClass(CodecSpecificData.class, properties);
        }

        /**
         * Read the Image data in Byte Stream Format (H265 Appendix B)
         * @return number of bytes read
         */
        public int readExtentAsByteStream(final int id, @NonNull RandomStreamReader streamReader,
                                        @NonNull ByteBuffer byteBuffer) throws IOException {
            final int inPosition = byteBuffer.position();
            final int bytes = readExtent(id, streamReader, byteBuffer);
            //Replace the length with start code prefix
            if (bytes > 4) {
                byteBuffer.putInt(inPosition, 0);
                byteBuffer.put(inPosition + 3, (byte)1);
            }
            return bytes;
        }

        /**
         * Read the extent into a ByteBuffer
         * @param id extent id
         * @param byteBuffer needs to be at least <code>getExtendSize(id)</code> long
         * @return number of bytes read
         */
        public int readExtent(final int id, @NonNull RandomStreamReader streamReader,
                                  @NonNull ByteBuffer byteBuffer) throws IOException {
            final long extentBytes = itemLocation.getExtentLength(id);
            if (extentBytes > byteBuffer.remaining()) {
                throw new BufferOverflowException();
            }
            final long position = itemLocation.getExtentOffset(id);
            final int inLimit = byteBuffer.limit();
            final int bytes;
            try {
                byteBuffer.limit(byteBuffer.position() + (int)extentBytes);
                bytes = streamReader.read(byteBuffer, position);
            } finally {
                byteBuffer.limit(inLimit);
            }
            return bytes;
        }

        /**
         * Get the {@link ItemLocation} for this Image
         */
        public ItemLocation getItemLocation() {
            return itemLocation;
        }
    }
}
