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
    private final Map<String, PropertyWrap> propertyWraps = new LinkedHashMap<>();

    private ClassWrap(TypeWrap typeWrap) {
        this.typeWrap = typeWrap;
        loadDeclaredFields();
        loadDeclaredPropertys();
    }

    public TypeWrap getTypeWrap() {
        return typeWrap;
    }

    public Map<String, PropertyWrap> getPropertyWraps() {
        return propertyWraps;
    }

    public PropertyWrap getPropertyWrap(String propertyName) {
        return propertyWraps.get(propertyName);
    }

    private void loadDeclaredFields() {
        Class<?> current = typeWrap.getType();

        while (current != null) {
            for (Field f : current.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                PropertyFieldWrap fieldWrap = new PropertyFieldWrap(typeWrap, f);

                propertyWraps.computeIfAbsent(fieldWrap.getName(), k -> new PropertyWrap(k))
                        .setFieldWrap(fieldWrap);
            }
            current = current.getSuperclass();
        }
    }

    private void loadDeclaredPropertys() {
        for (Method m : typeWrap.getType().getMethods()) {
            if(m.getDeclaringClass() == Object.class){
                continue;
            }

            if (m.getName().length() > 3) {
                if (m.getReturnType() == void.class && m.getParameterCount() == 1) {
                    //setter
                    if (m.getName().startsWith("set")) {
                        PropertyMethodWrap setterWrap = new PropertyMethodWrap(typeWrap, m);
                        propertyWraps.computeIfAbsent(setterWrap.getName(), k -> new PropertyWrap(k))
                                .setSetterWrap(setterWrap);
                    }
                } else if (m.getReturnType() != void.class && m.getParameterCount() == 0) {
                    //getter
                    if (m.getName().startsWith("get")) {
                        PropertyMethodWrap getterWrap = new PropertyMethodWrap(typeWrap, m);
                        propertyWraps.computeIfAbsent(getterWrap.getName(), k -> new PropertyWrap(k))
                                .setGetterWrap(getterWrap);
                    }
                }
            }
        }
    }
}
