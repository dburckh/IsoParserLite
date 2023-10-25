package com.homesoft.iso;

import java.io.IOException;

public interface DependantBoxReader extends DependantParser {
    Object read(Box box, StreamReader streamReader, Object dependency) throws IOException;
}
