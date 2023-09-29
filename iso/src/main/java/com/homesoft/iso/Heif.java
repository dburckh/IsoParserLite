package com.homesoft.iso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.box.Av1DecoderConfigBox;
import com.homesoft.iso.box.CodecSpecificData;
import com.homesoft.iso.box.BaseContainerBox;
import com.homesoft.iso.box.ExtentBox;
import com.homesoft.iso.box.FileTypeBox;
import com.homesoft.iso.box.HevcDecoderConfigBox;
import com.homesoft.iso.box.ImageSpatialExtents;
import com.homesoft.iso.box.ImageSpatialExtentsBox;
import com.homesoft.iso.box.ItemInfoEntry;
import com.homesoft.iso.box.ItemInfoBox;
import com.homesoft.iso.box.ItemLocation;
import com.homesoft.iso.box.ItemLocationBox;
import com.homesoft.iso.box.ItemPropertyAssociation;
import com.homesoft.iso.box.ItemPropertyAssociationBox;
import com.homesoft.iso.box.ItemReferenceBox;
import com.homesoft.iso.box.PrimaryItemParser;
import com.homesoft.iso.box.SingleItemTypeReference;
import com.homesoft.iso.box.StringBox;

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
    private static final Object[] EMPTY_ARRAY = new Object[0];
    public static final ContainerBox ROOT_PARSER = new BaseContainerBox()
            .addParser(new FileTypeBox())
            .addParser(TYPE_meta, new BaseContainerBox(true)
                    .addParser(TYPE_pitm, new PrimaryItemParser())
                    .addParser(TYPE_iinf, new ItemInfoBox())
                    .addParser(new ItemLocationBox())
                    .addParser(TYPE_iref, new ItemReferenceBox())
                    .addParser(TYPE_irpr, new BaseContainerBox()
                            .addParser(new ItemPropertyAssociationBox())
                            .addParser(TYPE_ipco, new BaseContainerBox()
                                    .addParser(new ImageSpatialExtentsBox())
                                    .addParser(new HevcDecoderConfigBox())
                                    .addParser(new Av1DecoderConfigBox())
                                    .addParser(TYPE_AUXC, new StringBox(true))
                                    .addParser(BaseContainerBox.TYPE_DEFAULT, new ExtentBox())
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

    public static Heif parse(File file) throws Exception {
        try (final StreamReader streamReader = IsoParser.newStreamReader(file)) {
            return parse(streamReader);
        }
    }
    /**
     * Parse a HEIF File using the default HEIF BoxParsers
     */
    public static Heif parse(final StreamReader streamReader) throws Exception {
        final TypeListener typeListener = new TypeListener(TYPE_meta);
        typeListener.addTypeListeners(TYPE_pitm, TYPE_iinf, TYPE_ipma, TYPE_iloc, TYPE_iref, TYPE_ipco);
        IsoParser.parse(ROOT_PARSER, streamReader, typeListener);
        return new Heif(typeListener);
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
    private Heif(final TypeListener typeListener) {
        primaryItemId = (Integer)typeListener.getType(TYPE_pitm);
        itemInfoEntries = StreamUtil.toArray(typeListener.getType(TYPE_iinf), ItemInfoEntry.class);
        itemPropertyAssociations = (ItemPropertyAssociation[])Objects.requireNonNull(typeListener.getType(TYPE_ipma));
        propertyArray = StreamUtil.toArray(typeListener.getType(TYPE_ipco), Object.class);
        final Object temp = typeListener.getType(TYPE_iref);
        itemReferences = temp == null ? new SingleItemTypeReference[0] : StreamUtil.toArray(temp, SingleItemTypeReference.class);
        itemLocations = (ItemLocation[]) Objects.requireNonNull(typeListener.getType(TYPE_iloc));
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

    @NonNull
    public Item getPrimaryItemId() throws InvalidObjectException {
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
            if (reference.type == ItemReferenceBox.TYPE_DIMG && reference.fromId == itemInfoEntry.id) {
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
