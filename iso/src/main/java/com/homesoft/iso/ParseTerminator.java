package com.homesoft.iso;

import java.util.List;

public interface ParseTerminator {
    /**
     *
     * @param list List of results
     * @param lastType last Box type parsed
     * @return true to terminate the parse function
     */
    boolean terminate(List<Object> list, int lastType);
}
