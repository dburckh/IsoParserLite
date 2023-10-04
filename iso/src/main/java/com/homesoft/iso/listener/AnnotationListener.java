package com.homesoft.iso.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.homesoft.iso.ClassResult;
import com.homesoft.iso.ParseListener;
import com.homesoft.iso.TypeResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Process {@link TypeResult} and {@link ClassResult} annotations
 */
public class AnnotationListener implements ParseListener {

    final private HashMap<Integer, AnnotationInfo> typeMap = new HashMap<>();
    final private HashMap<Class<?>, AnnotationInfo> classMap = new HashMap<>();

    private static ArrayList<AnnotationInfo> getAnnotationInfoList(@NonNull final Object object) {
        final ArrayList<AnnotationInfo> list = new ArrayList<>();
        for (final Method method : object.getClass().getMethods()) {
            final Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1) {
                final TypeResult typeResult = method.getAnnotation(TypeResult.class);
                if (typeResult != null) {
                    list.add( new MethodInfo(typeResult.value(), object, method));
                } else {
                    final ClassResult classResult = method.getAnnotation(ClassResult.class);
                    if (classResult != null) {
                        list.add(new MethodInfo(parameterTypes[0], object, method));
                        for (final Class<?> c : classResult.value()) {
                            list.add(new MethodInfo(c, object, method));
                        }
                    }
                }
            }
        }
        for (final Field field : object.getClass().getDeclaredFields()) {
            final TypeResult typeResult = field.getAnnotation(TypeResult.class);
            if (typeResult != null) {
                list.add(new FieldInfo(typeResult.value(), object, field));
            }
        }
        return list;
    }

    public AnnotationListener(@NonNull Object...objects) {
        for (Object o : objects) {
            add(o);
        }
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
            annotationInfo.set(result);
        }
    }
    private abstract static class AnnotationInfo {
        final Object key;
        final Object object;
        abstract void set(Object value);

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AnnotationInfo) {
                final AnnotationInfo annotationInfo = (AnnotationInfo) obj;
                return key.equals(annotationInfo.key) && object.equals(annotationInfo.object);
            }
            return false;
        }

        public AnnotationInfo(Object key, Object object) {
            this.key = key;
            this.object = object;
        }

        @Override
        public int hashCode() {
            return key.hashCode() + object.hashCode();
        }
    }
    private static class MethodInfo extends AnnotationInfo {
        final Method method;
        MethodInfo(@NonNull Integer key, @NonNull Object object, @NonNull Method method) {
            super(key, object);
            this.method = method;
        }
        MethodInfo(@NonNull Class<?> key, @NonNull Object object, @NonNull Method method) {
            super(key, object);
            this.method = method;
        }

        @Override
        public void set(Object value) {
            try {
                method.invoke(object, value);
            } catch (InvocationTargetException | IllegalAccessException e) {
                // Intentionally blank
            }
        }
    }

    private static class FieldInfo extends AnnotationInfo {
        final Field field;
        FieldInfo(@NonNull Integer key, @NonNull Object object, @NonNull Field field) {
            super(key, object);
            this.field = field;
        }

        @Override
        public void set(Object value) {
            try {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                field.set(object, value);
            } catch (IllegalAccessException e) {
                // Intentionally blank
            }
        }
    }
}
