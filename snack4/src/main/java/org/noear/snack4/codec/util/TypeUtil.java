/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.codec.util;


import org.noear.snack4.SnackException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 类型工具类
 *
 * @author noear
 * @since 4.0
 * */
public class TypeUtil {
    public final static BigInteger INT_LOW = BigInteger.valueOf(-9007199254740991L);
    public final static BigInteger INT_HIGH = BigInteger.valueOf(9007199254740991L);
    public final static BigDecimal DEC_LOW = BigDecimal.valueOf(-9007199254740991L);
    public final static BigDecimal DEC_HIGH = BigDecimal.valueOf(9007199254740991L);

    /**
     * 将字符串转为类型
     */
    public static Object strTo(String str, Class<?> clz) {
        if (Integer.class.isAssignableFrom(clz) || Integer.TYPE == clz) {
            return Integer.parseInt(str);
        } else if (Long.class.isAssignableFrom(clz) || Long.TYPE == clz) {
            return Long.parseLong(str);
        } else if (Enum.class.isAssignableFrom(clz)) {
            return Enum.valueOf((Class<? extends Enum>) clz, str);
        } else {
            throw new SnackException("Unsupport type '" + str + "', to: " + clz.getName());
        }
    }


    private static Map<String, EnumWrap> enumCached = new ConcurrentHashMap<>();

    public static EnumWrap createEnum(Class<?> clz) {
        String key = clz.getName();
        EnumWrap val = enumCached.get(key);
        if (val == null) {
            val = new EnumWrap(clz);
            enumCached.put(key, val);
        }

        return val;
    }


    public static Type getCollectionItemType(Type fieldType) {
        if (fieldType instanceof ParameterizedType) {
            return getCollectionItemType((ParameterizedType) fieldType);
        }
        if (fieldType instanceof Class<?>) {
            return getCollectionItemType((Class<?>) fieldType);
        }
        return Object.class;
    }

    private static Type getCollectionItemType(Class<?> clazz) {
        return clazz.getName().startsWith("java.")
                ? Object.class
                : getCollectionItemType(getCollectionSuperType(clazz));
    }

    private static Type getCollectionSuperType(Class<?> clazz) {
        Type assignable = null;
        for (Type type : clazz.getGenericInterfaces()) {
            Class<?> rawClass = getRawClass(type);
            if (rawClass == Collection.class) {
                return type;
            }
            if (Collection.class.isAssignableFrom(rawClass)) {
                assignable = type;
            }
        }
        return assignable == null ? clazz.getGenericSuperclass() : assignable;
    }

    private static Type getCollectionItemType(ParameterizedType parameterizedType) {
        Type rawType = parameterizedType.getRawType();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (rawType == Collection.class) {
            return getWildcardTypeUpperBounds(actualTypeArguments[0]);
        }
        Class<?> rawClass = (Class<?>) rawType;
        Map<TypeVariable, Type> typeParameterMap = createTypeParameterMap(rawClass.getTypeParameters(), actualTypeArguments);
        Type superType = getCollectionSuperType(rawClass);
        if (superType instanceof ParameterizedType) {
            Class<?> superClass = getRawClass(superType);
            Type[] superClassTypeParameters = ((ParameterizedType) superType).getActualTypeArguments();
            return superClassTypeParameters.length > 0
                    ? getCollectionItemType(makeParameterizedType(superClass, superClassTypeParameters, typeParameterMap))
                    : getCollectionItemType(superClass);
        }
        return getCollectionItemType((Class<?>) superType);
    }

    private static Map<TypeVariable, Type> createTypeParameterMap(TypeVariable[] typeParameters, Type[] actualTypeArguments) {
        int length = typeParameters.length;
        Map<TypeVariable, Type> typeParameterMap = new HashMap<TypeVariable, Type>(length);
        for (int i = 0; i < length; i++) {
            typeParameterMap.put(typeParameters[i], actualTypeArguments[i]);
        }
        return typeParameterMap;
    }

    private static ParameterizedType makeParameterizedType(Class<?> rawClass, Type[] typeParameters, Map<TypeVariable, Type> typeParameterMap) {
        int length = typeParameters.length;
        Type[] actualTypeArguments = new Type[length];
        System.arraycopy(typeParameters, 0, actualTypeArguments, 0, length);
        for (int i = 0; i < actualTypeArguments.length; i++) {
            Type actualTypeArgument = actualTypeArguments[i];
            if (actualTypeArgument instanceof TypeVariable) {
                actualTypeArguments[i] = typeParameterMap.get(actualTypeArgument);
            }
        }
        return new ParameterizedTypeImpl(rawClass, actualTypeArguments, null);
    }

    private static Type getWildcardTypeUpperBounds(Type type) {
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            Type[] upperBounds = wildcardType.getUpperBounds();
            return upperBounds.length > 0 ? upperBounds[0] : Object.class;
        }

//        if (type instanceof ParameterizedType) {
//            return ((ParameterizedType) type).getRawType();
//        }

        return type;
    }

    public static boolean isEmptyCollection(Object obj) {
        if (obj == null || obj == Collections.EMPTY_MAP || obj == Collections.EMPTY_LIST || obj == Collections.EMPTY_SET) {
            return true;
        } else {
            return false;
        }
    }

    public static Collection createCollection(Type type, boolean isThrow) {
        if (type == null) {
            return new ArrayList();
        }

        //最常用的放前面
        if (type == ArrayList.class) {
            return new ArrayList();
        }

        Class<?> rawClass = getRawClass(type);
        Collection list;
        if (rawClass == AbstractCollection.class //
                || rawClass == Collection.class) {
            list = new ArrayList();
        } else if (rawClass.isAssignableFrom(HashSet.class)) {
            list = new HashSet();
        } else if (rawClass.isAssignableFrom(LinkedHashSet.class)) {
            list = new LinkedHashSet();
        } else if (rawClass.isAssignableFrom(TreeSet.class)) {
            list = new TreeSet();
        } else if (rawClass.isAssignableFrom(ArrayList.class)) {
            list = new ArrayList();
        } else if (rawClass.isAssignableFrom(EnumSet.class)) {
            Type itemType;
            if (type instanceof ParameterizedType) {
                itemType = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                itemType = Object.class;
            }
            list = EnumSet.noneOf((Class<Enum>) itemType);
        } else {
            try {
                list = (Collection) rawClass.getDeclaredConstructor().newInstance();
            } catch (Throwable e) {
                if (isThrow) {
                    throw new SnackException("The instantiation failed, class: " + rawClass.getName(), e);
                } else {
                    return null;
                }
            }
        }
        return list;
    }


    public static Map createMap(Type type) {
        if (type == null) {
            return new HashMap();
        }

        //最常用的放前面
        if (type == HashMap.class) {
            return new HashMap();
        }

        if (type == Properties.class) {
            return new Properties();
        }

        if (type == Hashtable.class) {
            return new Hashtable();
        }

        if (type == IdentityHashMap.class) {
            return new IdentityHashMap();
        }

        if (type == SortedMap.class || type == TreeMap.class) {
            return new TreeMap();
        }

        if (type == ConcurrentMap.class || type == ConcurrentHashMap.class) {
            return new ConcurrentHashMap();
        }

        if (type == LinkedHashMap.class) {
            return new LinkedHashMap();
        }

        if (type == Map.class) {
            return new HashMap();
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            Type rawType = parameterizedType.getRawType();
            if (EnumMap.class.equals(rawType)) {
                Type[] actualArgs = parameterizedType.getActualTypeArguments();
                return new EnumMap((Class) actualArgs[0]);
            }

            return createMap(rawType);
        }

        Class<?> clazz = (Class<?>) type;
        if (clazz.isInterface()) {
            throw new SnackException("Unsupport type, class: " + type);
        }

        try {
            return (Map) clazz.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            throw new SnackException("Unsupport type, class: " + type, e);
        }
    }

    public static Class<?> getRawClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getRawClass(((ParameterizedType) type).getRawType());
        } else {
            throw new SnackException("Unsupport type, class: " + type);
        }
    }
}
