# IsoParserLite
A lightweight ISO BMFF (MP4) Container Parser

This is a lightweight stream based ISO BMFF (.mp4, .m4a, .mpv, .heic, .avif, .cr3) parser.  

## Getting Started
- The main module for most people is iso.  It pure Java.
- Movie, Heif and CanonRaw3 are the classes you want to look at.  See the tests for concrete usage examples.
- StringParseTest has a sample that creates a Heirarchy which is easy to read.  This can parse normal MP4 files (.mp4, .m4a, .mpv)

## Proguard
If you are using Proguard, it will strip the annotated attributes as they are set by reflection.  Add this to your .pro file to keep them.
~~~
-keepclassmembers,allowobfuscation class * {
 @com.homesoft.iso.TypeResult <fields>;
 @com.homesoft.iso.ClassResult <fields>;
}
~~~

### Why would you use this vs the others?
- It uses seperate parsing and result (DTO) classes to provide an easily manipulatable and reusable parsing structure.
- It has short-circuited parsing to reduce IO.
- It has a highly flexible parsing structure.
- It should require less code for custom parsers.
- It is stream based (callback).  This allows you to inject custom parsing outside the framework.

### Why would you not use this library?
- It is not nearly as complete as [mp4parser](https://github.com/sannies/mp4parser) or [GPAC/mp4mx](https://github.com/gpac/gpac/wiki/mp4mx).
- If you need to write ISO BMFF, look elsewhere.
- I don't have the specs for the many OSI BMFF standards required to be 100% complete.  They cost $$$.  A lot of the code is reverse engineered from the above libraries and the Chromium source code.  Heif and CR3 sources are sited in the code.
