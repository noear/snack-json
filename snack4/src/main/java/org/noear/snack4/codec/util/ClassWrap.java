package org.noear.snack4.codec.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
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
    private final Map<String, FieldWrap> fieldWraps;
    private final Map<String, PropertyWrap> propertyWraps;

    private ClassWrap(TypeWrap typeWrap) {
        this.typeWrap = typeWrap;

        this.fieldWraps = getDeclaredFields(typeWrap);
        this.propertyWraps = getDeclaredPropertys(typeWrap);
    }

    public TypeWrap getTypeWrap() {
        return typeWrap;
    }

    public Map<String, FieldWrap> getFieldWraps() {
        return fieldWraps;
    }

    public Map<String, PropertyWrap> getPropertyWraps() {
        return propertyWraps;
    }

    private static Map<String, FieldWrap> getDeclaredFields(TypeWrap typeWrap) {
        Map<String, FieldWrap> fields = new LinkedHashMap<>();
        Class<?> current = typeWrap.getType();
        while (current != null) {
            for (Field f : current.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                fields.put(f.getName(), new FieldWrap(typeWrap, f));
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    public static Map<String, PropertyWrap> getDeclaredPropertys(TypeWrap typeWrap) {
        Map<String, PropertyWrap> methods = new LinkedHashMap<>();
        for (Method m : typeWrap.getType().getMethods()) {
            if (m.getName().length() > 3) {
                if (m.getReturnType() == void.class && m.getParameterCount() == 1) {
                    //setter
                    if (m.getName().startsWith("set")) {
                        methods.put(m.getName(), new PropertyWrap(typeWrap, m));
                    }
                } else if (m.getReturnType() != void.class && m.getParameterCount() == 0) {
                    //getter
                    if (m.getName().startsWith("get")) {
                        methods.put(m.getName(), new PropertyWrap(typeWrap, m));
                    }
                }
            }
        }

        return methods;
    }
}
