package com.homesoft.iso;

public interface BoxTypes {
    int TYPE_ftyp = 0x66747970; //ftyp
    int TYPE_uuid = 0x75756964; //uuid

    int TYPE_meta = 0x6D657461; //meta
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
