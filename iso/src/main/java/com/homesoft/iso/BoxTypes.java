package com.homesoft.iso;

import com.homesoft.iso.box.Data;
import com.homesoft.iso.box.Extent;
import com.homesoft.iso.box.SetIndex;

public interface BoxTypes {
    int TYPE_NA = 0; // Not applicable tag (null).
    int TYPE_ftyp = 0x66747970; //ftyp

    int TYPE_moov = 0x6D6F6F76; //moov - Movie
    int TYPE_mvhd = 0x6D766864; //moov->mvhd - Movie Header
    int TYPE_trak = 0x7472616B; //moov->trak - Track
    int TYPE_tkhd = 0x746B6864; //moov->trak->tkhd - Track Header
    int TYPE_mdia = 0x6D646961; //moov->trak->mdia - Media
    int TYPE_mdhd = 0x6D646864; //moov->trak->mdia->mdhd - Media Header
    int TYPE_hdlr = 0x68646C72; //moov->trak->mdia->hdlr - Handler
    int TYPE_minf = 0x6D696E66; //moov->trak->mdia->minf - Media Information
    int TYPE_stbl = 0x7374626C; //moov->trak->mdia->minf->stbl - Sample Table Box
    int TYPE_stsd = 0x73747364; //moov->trak->mdia->minf->stbl->stsd - Sample Descriptions
    int TYPE_hvc1 = 0x68766331; //moov->trak->mdia->minf->stbl->stsd->hvc1 - HEVC description
    int TYPE_avcC = 0x61766343; //moov->trak->mdia->minf->stbl->stsd->avc1-> avcC - AVC Codec Config
    int TYPE_udta = 0x75647461; //moov->udta
    int TYPE_ilst = 0x696C7374; //moov->udta->ilst

    //-------------------------------------------------------------------------
    // iTunes metadata item list
    // https://developer.apple.com/documentation/quicktime-file-format/metadata_item_list_atom
    //-------------------------------------------------------------------------

    /**
     * Album
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    int TYPE_Aalb = 0xA9616C62; //@alb
    /**
     * Album Artist
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    int TYPE_AART = 0xA9415254; //©ART
    /**
     * Year, obviously.
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    int TYPE_Aday = 0xA9646179; //@day
    /**
     * Title
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    int TYPE_Anam = 0xA96E616D; //@nam
    /**
     * Encoder, obviously
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    int TYPE_Atoo = 0xA9746F6F; //@too
    /**
     * Composer
     * Returned as {@link Data} with {@link Data#data} as {@link String}
     */
    int TYPE_Awrt = 0xA9777274; //@wrt
    /**
     * Cover art.
     * Returned as {@link Data}.
     * {@link Data#dataType} will be {@link Data#JPEG} or {@link Data#JPEG}
     * {@link Data#data} will be an {@link Extent} pointing to the image
     */
    int TYPE_covr = 0x636F7672;
    /**
     * Disk x of y.
     * Returned as {@link SetIndex}
     */
    int TYPE_disk = 0x6469736B;
    /**
     * Genre - Appears to be an int.  Can't find a decode table anywhere
     * Returned as {@link Number}
     */
    int TYPE_gnre = 0x676E7265;
    /**
     * Tempo - beats per minute
     * Returned as {@link Number}
     */
    int TYPE_tmpo = 0x746D706F;
    /**
     * Track number x of y
     * {@link SetIndex}
     */
    int TYPE_trkn = 0x74726B6E;

    int TYPE_data = 0x64617461; //moov->udta->ilst->???->data
    int TYPE_uuid = 0x75756964; //uuid

    int TYPE_meta = 0x6D657461; //moov->udta->meta (Media) or meta (Heif)
    int TYPE_iloc = 0x696C6F63; //meta->iloc
    int TYPE_iinf = 0x69696E66; //meta->iinf
    int TYPE_infe = 0x696E6665; //meta->iinf->infe

    int TYPE_pitm = 0x7069746D; //meta->pitm

    int TYPE_iref = 0x69726566; //meta->iref
    //HEIF types
    int TYPE_irpr = 0x69707270; //meta->irpr
    int TYPE_ipma = 0x69706D61; //meta->irpr->ipma

    int TYPE_ipco = 0x6970636F; //meta->irpr->ipco

    int TYPE_AUXC = 0x61757843; //meta->irpr->ipco->auxC

    int TYPE_PIXI = 0x70697869; //meta->irpr->ipco->pixi

    int TYPE_pasp = 0x70617370; // meta->irpr->ipco->pasp - Pixel Aspect Ratio
    int TYPE_hvcC = 0x68766343; // meta->irpr->ipco->hvcC - HEVC Codec Config
    int TYPE_av1C = 0x61763143; // meta->irpr->ipco->av1C - AV1 Codec Config
    int TYPE_ispe = 0x69737065; // meta->irpr->ipco->ispe - Image Spacial Extents (dimensions)

}
