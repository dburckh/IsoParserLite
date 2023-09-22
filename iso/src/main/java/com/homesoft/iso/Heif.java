package com.homesoft.iso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.parser.Av1DecoderConfigParser;
import com.homesoft.iso.parser.CodecSpecificData;
import com.homesoft.iso.parser.GenericContainerParser;
import com.homesoft.iso.parser.FileType;
import com.homesoft.iso.parser.FileTypeParser;
import com.homesoft.iso.parser.HevcDecoderConfigParser;
import com.homesoft.iso.parser.ImageSpatialExtents;
import com.homesoft.iso.parser.ImageSpatialExtentsParser;
import com.homesoft.iso.parser.ItemInfoEntry;
import com.homesoft.iso.parser.ItemInfoParser;
import com.homesoft.iso.parser.ItemLocation;
import com.homesoft.iso.parser.ItemLocationParser;
import com.homesoft.iso.parser.ItemPropertyAssociation;
import com.homesoft.iso.parser.ItemPropertyAssociationParser;
import com.homesoft.iso.parser.ItemReferenceParser;
import com.homesoft.iso.parser.NumberType;
import com.homesoft.iso.parser.ObjectsType;
import com.homesoft.iso.parser.PrimaryItemParser;
import com.homesoft.iso.parser.SingleItemTypeReference;
import com.homesoft.iso.parser.StringParser;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parser for HEIF based ISOBMFF files
 * This includes HEIC (HEVC) and AVIF (AV1) image files
 */
public class Heif {
    public static final ContainerParser ROOT_PARSER = new GenericContainerParser()
            .addParser(new FileTypeParser())
            .addParser(BoxTypes.TYPE_meta, new GenericContainerParser(true, false)
                    .addParser(new PrimaryItemParser())
                    .addParser(BoxTypes.TYPE_iinf, new ItemInfoParser())
                    .addParser(new ItemLocationParser())
                    .addParser(BoxTypes.TYPE_iref, new ItemReferenceParser())
                    .addParser(BoxTypes.TYPE_irpr, new GenericContainerParser()
                            .addParser(new ItemPropertyAssociationParser())
                            .addParser(BoxTypes.TYPE_ipco, new GenericContainerParser(false, true)
                                    .addParser(new ImageSpatialExtentsParser())
                                    .addParser(new HevcDecoderConfigParser())
                                    .addParser(new Av1DecoderConfigParser())
                                    .addParser(BoxTypes.TYPE_AUXC, new StringParser(true))
                            )
                    )
            );

    public static void main(String[] args) {
        final File file = new File("C:\\Users\\dburc\\Pictures\\heic\\20230907_162647.heic");
        try {
            final Heif heif = parse(file);
            System.out.println(heif);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse a HEIF File using the default HEIF BoxParsers
     */
    public static Heif parse(File file) throws Exception {
        try (final StreamReader streamReader = IsoParser.newStreamReader(file)) {
            return parse(streamReader);
        }
    }

    /**
     * Parse a HEIF StreamReader using the default HEIF BoxParsers
     */
    public static Heif parse(StreamReader streamReader) throws IOException {
        Object[] objects = IsoParser.parse(ROOT_PARSER, streamReader);
        return new Heif(objects);
    }

    @NonNull
    private final Integer primaryItem;

    private final FileType fileType;
    private final ItemInfoEntry[] itemInfoEntries;
    private final ItemPropertyAssociation itemPropertyAssociation;
    private final Object[] propertyArray;

    private final SingleItemTypeReference[] references;

    private final ItemLocation[] itemLocations;

    /**
     * Private constructor that takes the output of the IsoParser
     * and turns it into user friendly objects
     * @param objects data from the IsoParser
     */
    private Heif(final Object[] objects) {
        fileType = DataUtil.findClass(FileType.class, objects);
        final ObjectsType metaObjects = (ObjectsType) DataUtil.findType(BoxTypes.TYPE_meta, objects);
        NumberType primaryItem = null;
        ItemLocation[] itemLocations = new ItemLocation[0];
        ItemInfoEntry[] itemInfoEntries = new ItemInfoEntry[0];
        SingleItemTypeReference[] references = new SingleItemTypeReference[0];
        ObjectsType itemPropertyObjectsType = null;

        if (metaObjects != null) {
            for (Object object : metaObjects.objects) {
                if (object instanceof Type) {
                    switch (((Type) object).getType()) {
                        case BoxTypes.TYPE_pitm:
                            primaryItem = (NumberType) object;
                            break;
                        case BoxTypes.TYPE_irpr:
                            itemPropertyObjectsType = (ObjectsType) object;
                            break;
                    }
                    if (((Type) object).getType() == BoxTypes.TYPE_pitm) {
                        primaryItem = (NumberType) object;
                    }
                } else {
                    final Class<?> c = object.getClass();
                    if (c == ItemLocation[].class) {
                        itemLocations = (ItemLocation[]) object;
                    } else if (c == ItemInfoEntry[].class) {
                        itemInfoEntries = (ItemInfoEntry[]) object;
                    } else if (c == SingleItemTypeReference[].class) {
                        references = (SingleItemTypeReference[]) object;
                    }
                }
            }
        }
        this.itemInfoEntries = itemInfoEntries;
        this.references = references;
        this.itemLocations = itemLocations;
        if (primaryItem == null) {
            throw new NullPointerException("Expected Primary Item");
        }
        this.primaryItem = primaryItem.number.intValue();

        ObjectsType propertyObjectsType = null;
        ItemPropertyAssociation itemPropertyAssociation = null;
        if (itemPropertyObjectsType != null) {
            for (Object object : itemPropertyObjectsType.objects) {
                if (object instanceof Type) {
                    if (((Type) object).getType() == BoxTypes.TYPE_ipco) {
                        propertyObjectsType = (ObjectsType) object;
                    }
                } else if (object instanceof ItemPropertyAssociation) {
                    itemPropertyAssociation = (ItemPropertyAssociation) object;
                }
            }
        }
        propertyArray = propertyObjectsType == null ? IsoParser.OBJECT_ARRAY : propertyObjectsType.objects;
        if (itemPropertyAssociation == null) {
            throw new NullPointerException("Item Properties Associations Required");
        }
        this.itemPropertyAssociation = itemPropertyAssociation;

    }

    private Object[] getProperties(int id) {
        final int[] propertyIds = itemPropertyAssociation.getAssociations(id);
        if (propertyIds.length == 0 || propertyIds[0] == 0) {
            return TypedWrapper.EMPTY_ARRAY;
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
        final ItemInfoEntry itemInfoEntry = DataUtil.findId(id, itemInfoEntries);
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
        final ItemLocation itemLocation = DataUtil.findId(itemInfoEntry.id, itemLocations);
        if (itemLocation == null) {
            return null;
        }
        final Object[] properties = getProperties(itemInfoEntry.id);
        return new Image(itemInfoEntry, properties, itemLocation);
    }

    @NonNull
    public Item getPrimaryItem() throws InvalidObjectException {
        final ItemInfoEntry itemInfoEntry = DataUtil.findId(primaryItem, itemInfoEntries);
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
        for (SingleItemTypeReference reference : references) {
            if (reference.type == ItemReferenceParser.TYPE_DIMG && reference.fromId == itemInfoEntry.id) {
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

        Type getProperty(int type) {
            return DataUtil.findType(type, properties);
        }

        /**
         * Get the <code>ImageSpatialExtents</code> aka dimensions for this image
         */
        public ImageSpatialExtents getImageSpatialExtents() {
            Object data = getProperty(BoxTypes.TYPE_ispe);
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
        public ByteBuffer getCodecSpecificData() {
            final CodecSpecificData codecSpecificData = DataUtil.findClass(CodecSpecificData.class, properties);
            if (codecSpecificData != null) {
                return codecSpecificData.getCSDByteBuffer();
            }
            return null;
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
