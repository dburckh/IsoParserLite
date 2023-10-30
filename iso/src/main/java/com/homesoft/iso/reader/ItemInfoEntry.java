package com.homesoft.iso.reader;

import androidx.annotation.NonNull;

import com.homesoft.iso.Box;
import com.homesoft.iso.Id;
import com.homesoft.iso.Type;

public class ItemInfoEntry implements Type, Id {
    public static final int ITEM_TYPE_mime = 0x6d696d65;
    public static final int ITEM_TYPE_exif = 0x45786966;
    public static final int ITEM_TYPE_grid = 0x67726964; // grid - Image composed of other images
    public static final int ITEM_TYPE_av01 = 0x61763031; // av01 - Standalone image

    public static final int ITEM_TYPE_hvc1 = 0x68766331; // hvc1 - Standalone image

    public static final String CONTENT_TYPE_XMP = "application/rdf+xml";

    public static final int ITEM_TYPE_NA = Integer.MIN_VALUE;
    public final int id;

    public final short protectionIndex;
    public String name;
    public String contentType;
    public String contentEncoding;

    // Only version >= 2;
    public final int type;

    public ItemInfoEntry(int id, short protectionIndex, @NonNull String name, String contentType,
                         String contentEncoding, int itemType) {
        this.id = id;
        this.protectionIndex = protectionIndex;
        this.name = name;
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
        this.type = itemType;
    }

    /**
     * This is the Type of the item, not the box
     */
    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ItemInfoEntry{id=");
        sb.append(id);
        if (!name.isEmpty()) {
            sb.append(", name=");
            sb.append(name);
        }
        if (type != ITEM_TYPE_NA) {
            sb.append(", itemType=");
            sb.append(Box.typeToString(type));
        }
        sb.append('}');
        return sb.toString();
    }
}
