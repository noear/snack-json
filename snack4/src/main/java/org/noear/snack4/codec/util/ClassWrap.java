package org.noear.snack4.codec.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author noear 2025/10/7 created
 *
 */
public class ClassWrap {
    private static final Map<Type, ClassWrap> cached = new ConcurrentHashMap<>();

    public static ClassWrap from(TypeWrap typeWrap) {
        return cached.computeIfAbsent(typeWrap.getGenericType(), t -> new ClassWrap(typeWrap));
    }

    private final TypeWrap typeWrap;
    private final Collection<FieldWrap> fieldWraps;

    private ClassWrap(TypeWrap typeWrap) {
        this.typeWrap = typeWrap;

        this.fieldWraps = getDeclaredFields(typeWrap);
    }

    public TypeWrap getTypeWrap() {
        return typeWrap;
    }

    public Collection<FieldWrap> getFieldWraps() {
        return fieldWraps;
    }

    private static Collection<FieldWrap> getDeclaredFields(TypeWrap typeWrap) {
        Map<String, FieldWrap> fields = new LinkedHashMap<>();
        Class<?> current = typeWrap.getType();
        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);
                fields.put(field.getName(), new FieldWrap(typeWrap, field));
            }
            current = current.getSuperclass();
        }
        return fields.values();
    }
}
