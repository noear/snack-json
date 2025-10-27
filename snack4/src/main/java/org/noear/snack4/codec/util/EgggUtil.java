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

import org.noear.eggg.*;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.annotation.ONodeCreator;

import java.lang.reflect.*;

/**
 *
 * @author noear 2025/10/21 created
 * @since 4.0
 */
public class EgggUtil {
    private static final Eggg eggg = new Eggg()
            .withCreatorClass(ONodeCreator.class)
            .withDigestHandler(EgggUtil::doDigestHandle)
            .withAliasHandler(EgggUtil::doAliasHandle);

    private static String doAliasHandle(ClassEggg cw, AnnotatedEggg s, String ref) {
        if (s.getDigest() instanceof ONodeAttrHolder) {
            return ((ONodeAttrHolder) s.getDigest()).getAlias();
        } else {
            return ref;
        }
    }

    private static Object doDigestHandle(ClassEggg cw, AnnotatedEggg s, Object ref) {
        ONodeAttr attr = s.getElement().getAnnotation(ONodeAttr.class);

        if (attr == null && ref != null) {
            return ref;
        }

        if (s instanceof FieldEggg) {
            return new ONodeAttrHolder(attr, ((Field) s.getElement()).getName());
        } else if (s instanceof PropertyMethodEggg) {
            return new ONodeAttrHolder(attr, Property.resolvePropertyName(((Method) s.getElement()).getName()));
        } else if (s instanceof ParamEggg) {
            return new ONodeAttrHolder(attr, ((Parameter) s.getElement()).getName());
        } else {
            return null;
        }
    }

    /**
     * 获取类型包装器
     */
    public static TypeEggg getTypeEggg(Type type) {
        return eggg.getTypeEggg(type);
    }
}