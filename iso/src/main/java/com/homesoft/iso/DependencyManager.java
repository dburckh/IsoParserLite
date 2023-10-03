package com.homesoft.iso;

public interface DependencyManager {
    DependencyManager NULL = new DependencyManager() {
        @Override
        public void addDependency(Box parent, Listener<?> listener) {
            //Intentionally blank
        }

        @Override
        public void updateDependencies(Box parent, Object result) {
            //Intentionally blank
        }
    };

    /**
     * Add a dependency to a Box {@link Box}
     * @param parent when {@link Box#read(BoxHeader, StreamReader, int)} is called the listener
     *               will be notified
     * @param listener called with the result of {@link Box#read(BoxHeader, StreamReader, int)}
     */
    void addDependency(Box parent, Listener<?> listener);

    /**
     * Notify listeners of the result of the {@link Box} read(...)
     * @param parent {@link Box} that was read
     * @param result result of the {@link Box#read(BoxHeader, StreamReader, int)}
     */
    void updateDependencies(Box parent, Object result);

    interface Listener<T> {
        void onResult(T result);
    }
}
