package com.homesoft.iso;

public interface ContainerParser extends BoxParser {
    BoxParser getParser(int type);
}
