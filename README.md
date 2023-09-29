# IsoParserLite
A lightweight ISO BMFF (MP4) Container Parser

This is a lightweight stream based ISO BMFF (.mp4, .m4a, .mpv, .heic, .avif) parser.  

## Getting Started
- The main module for most people is iso.  It pure Java with none of that Kotlin funny business.
- Heif and Media are the classes you want to look at.  They both have a main() you can run, you will need your own test files.
- Media has a sample that creates a Heirarchy which is easy to look at.  This can parse normal MP4 files (.mp4, .m4a, .mpv)
- Heif has some higher level objects that make it easier to with the parsed data.  It's useful for HEIF files (.heic, .avif)

## Under construction!!!
I'm in active development.  
There is an Android module(app) that I was using to testing the HEIF libraries.  I was nerding out using MediaCodec to decode HEIC/AVIF.  Don't do it this way!  Use ImageDocoder.  At some point, I'll break off the Android head and move it to another repo.

### Why would you use this vs the big boys?
- It uses seperate parsing and result (DTO) classes to provide an easily reusable parsing structure
- It has short circuited parsing to reduce IO.
- It has a highly flexible parsing structure.
- It should require less code for custom parsers
- It is stream based (callback)

### Why would you not use this library?
- It is not nearly as complete as [mp4parser](https://github.com/sannies/mp4parser) or [GPAC/mp4mx](https://github.com/gpac/gpac/wiki/mp4mx).
- You need to write ISO BMFF
- I don't have the specs for the many OSI BMFF standards required to do this right.  They cost $$$.  A lot of the code is reverse engineered from the above libraries and the Chromium source code.
- It's newer and probably buggier.
