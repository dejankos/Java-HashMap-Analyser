package io.github.dejankos.hashmap.analyser.util;


import java.lang.reflect.Field;

import static java.util.Objects.isNull;

public class FieldUtils {

    public static <T> T readField(Object target, String fieldName, Class<T> valueType) throws NoSuchFieldException, IllegalAccessException {
        Field field = getField(target, fieldName);
        Object fieldValue = field.get(target);

        return convertValue(fieldValue, valueType);
    }

    public static Field getField(Object target, String fieldName) throws NoSuchFieldException {
        Field field = target.getClass().getDeclaredField(fieldName);
        if (!field.canAccess(target)) {
            field.setAccessible(true);
        }

        return field;
    }

    private static <T> T convertValue(Object value, Class<T> valueType) {
        if (isNull(value)) {
            return null;
        }

        if (!valueType.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot convert from %s to %s",
                            value.getClass().getSimpleName(),
                            valueType.getSimpleName()
                    )
            );
        }

        //noinspection unchecked
        return (T) value;
    }
}