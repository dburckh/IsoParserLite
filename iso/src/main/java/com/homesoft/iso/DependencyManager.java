package com.homesoft.iso;

public interface DependencyManager {
    DependencyManager NULL = new DependencyManager() {
        @Override
        public void add(BoxParser parent) {
            //Intentionally blank
        }

        @Override
        public boolean contains(BoxParser boxReader) {
            return false;
        }
    };

    /**
     * Add a dependency to a Box {@link BoxParser}
     */
    void add(BoxParser parent);

    boolean contains(BoxParser boxReader);
}
