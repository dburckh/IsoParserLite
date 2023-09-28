package com.homesoft.iso.box;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.Box;
import com.homesoft.iso.BoxHeader;
import com.homesoft.iso.BoxTypes;
import com.homesoft.iso.ContainerBox;
import com.homesoft.iso.StreamReader;
import com.homesoft.iso.TypedBox;

import java.io.IOException;
import java.util.Arrays;

/**
 * iTunes metadata item list
 * <a href="https://developer.apple.com/documentation/quicktime-file-format/metadata_item_list_atom">Spec</a>
 */
public class ItemListBox implements ContainerBox, TypedBox {
    /**
     * Album
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    public static final int TYPE_Aalb = 0xA9616C62; //@alb
    /**
     * Album Artist
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    public static final int TYPE_AART = 0xA9415254; //©ART
    /**
     * Year, obviously.
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    public static final int TYPE_Aday = 0xA9646179; //@day
    /**
     * Title
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    public static final int TYPE_Anam = 0xA96E616D; //@nam
    /**
     * Encoder, obviously
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    public static final int TYPE_Atoo = 0xA9746F6F; //@too
    /**
     * Composer
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    public static final int TYPE_Awrt = 0xA9777274; //@wrt
    /**
     * Cover art.
     * Returned as {@link Data}.
     * {@link Data#dataType} will be {@link Data#JPEG} or {@link Data#JPEG}
     * {@link Data#data} will be an {@link Extent} pointing to the image
     */
    public static final int TYPE_covr = 0x636F7672;
    /**
     * Disk x of y.
     * Returned as {@link SetIndex}
     */
    public static final int TYPE_disk = 0x6469736B;
    /**
     * Genre - Appears to be an int.  Can't find a decode table anywhere
     * Returned as {@link Number}
     */
    public static final int TYPE_gnre = 0x676E7265;
    /**
     * Tempo - beats per minute
     * Returned as {@link Number}
     */
    public static final int TYPE_tmpo = 0x746D706F;
    /**
     * Track number x of y
     * {@link SetIndex}
     */
    public static final int TYPE_trkn = 0x74726B6E;

    private static final int[] EMPTY_INTS = new int[0];
    private final int[] types;
    private static final ITunesItemBox ITUNES_ITEM_BOX = new ITunesItemBox();

    /**
     * Constructor that parses all tags
     */
    public ItemListBox() {
        this(EMPTY_INTS);
    }

    /**
     * Constructor that takes types the client cares about.
     * Full list of tags here:
     * <a href="https://atomicparsley.sourceforge.net/mpeg-4files.html">AtomicParsley iTunes Types</a>
     */
    public ItemListBox(@NonNull int ... types) {
        this.types = types;
        Arrays.sort(this.types);
    }

    @Override
    public boolean isFullBox() {
        return false;
    }

    @Override
    public Object read(BoxHeader boxHeader, StreamReader streamReader, int versionFlags) throws IOException {
        return null;
    }

    @Nullable
    @Override
    public Box getBox(int type) {
        if (types.length == 0 || Arrays.binarySearch(types, type) >= 0) {
            return ITUNES_ITEM_BOX;
        }
        return null;
    }

    @Override
    public int getType() {
        return BoxTypes.TYPE_ilst;
    }
}
