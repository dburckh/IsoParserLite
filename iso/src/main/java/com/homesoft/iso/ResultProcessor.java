package com.homesoft.iso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Process {@link TypeResult} and {@link ClassResult} annotations
 */
public class ResultProcessor implements ParseListener {

    final private HashMap<Integer, AnnotationInfo> typeMap = new HashMap<>();
    final private HashMap<Class<?>, AnnotationInfo> classMap = new HashMap<>();

    private static ArrayList<AnnotationInfo> getAnnotationInfoList(@NonNull final Object object) {
        final ArrayList<AnnotationInfo> list = new ArrayList<>();
        for (final Method method : object.getClass().getMethods()) {
            final Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1) {
                final TypeResult typeResult = method.getAnnotation(TypeResult.class);
                if (typeResult != null) {
                    list.add( new AnnotationInfo(typeResult.value(), object, method));
                } else {
                    final ClassResult classResult = method.getAnnotation(ClassResult.class);
                    if (classResult != null) {
                        list.add(new AnnotationInfo(parameterTypes[0], object, method));
                        for (final Class<?> c : classResult.value()) {
                            list.add(new AnnotationInfo(c, object, method));
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * Add an object to the annotation processor.
     * Existing annotations will be overwritten.
     * @param object any object with {@link TypeResult} or {@link ClassResult} annotations
     * @return true if annotations where found
     */
    public boolean add(@NonNull final Object object) {
        final ArrayList<AnnotationInfo> list = getAnnotationInfoList(object);
        if (list.isEmpty()) {
            return false;
        }
        for (AnnotationInfo annotationInfo : list) {
            if (annotationInfo.key instanceof Integer) {
                typeMap.put((Integer) annotationInfo.key, annotationInfo);
            } else if (annotationInfo.key instanceof Class<?>) {
                classMap.put((Class<?>) annotationInfo.key, annotationInfo);
            }
        }
        return true;
    }

    public boolean remove(@NonNull final Object object) {
        final ArrayList<AnnotationInfo> list = getAnnotationInfoList(object);
        boolean modified = false;
        for (AnnotationInfo annotationInfo : list) {
            if (annotationInfo.key instanceof Integer) {
                modified |= typeMap.remove(annotationInfo.key) != null;
            } else if (annotationInfo.key instanceof Class<?>) {
                modified |= classMap.remove((Class<?>) annotationInfo.key) != null;
            }
        }
        return modified;
    }

    @Override
    public void onContainerStart(int type, Object result) {
        onResult(type, result);
    }

    @Override
    public void onParsed(int type, Object result) {
        onResult(type, result);
    }

    @Override
    public void onContainerEnd(int type) {
        //Intentionally blank
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    public void onResult(int type, @Nullable Object result) {
        AnnotationInfo annotationInfo = typeMap.get(type);
        if (annotationInfo == null && result != null) {
            annotationInfo = classMap.get(result.getClass());
        }
        if (annotationInfo != null) {
            try {
                annotationInfo.method.invoke(annotationInfo.object, result);
            } catch (InvocationTargetException | IllegalAccessException e) {
                // Intentionally blank
            }
        }
    }

    private static class AnnotationInfo {
        final Object key;
        final Object object;
        final Method method;
        AnnotationInfo(@NonNull Integer key, @NonNull Object object, @NonNull Method method) {
            this.key = key;
            this.object = object;
            this.method = method;
        }
        AnnotationInfo(@NonNull Class<?> key, @NonNull Object object, @NonNull Method method) {
            this.key = key;
            this.object = object;
            this.method = method;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AnnotationInfo) {
                final AnnotationInfo annotationInfo = (AnnotationInfo) obj;
                return key.equals(annotationInfo.key) && object.equals(annotationInfo.object);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return key.hashCode() + object.hashCode();
        }
    }
}
