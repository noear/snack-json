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

import org.noear.eggg.ClassWrap;
import org.noear.eggg.Eggg;
import org.noear.eggg.Property;
import org.noear.eggg.TypeWrap;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.annotation.ONodeCreator;

import java.lang.reflect.*;

/**
 *
 * @author noear 2025/10/21 created
 *
 */
public class EgggUtil {
    private static final Eggg eggg = new Eggg()
            .withCreatorClass(ONodeCreator.class)
            .withAttachHandler(EgggUtil::doAttachmentHandle)
            .withAliasHandler(EgggAttach::getAlias);

    private static EgggAttach doAttachmentHandle(ClassWrap cw, Object h, AnnotatedElement e, EgggAttach ref) {
        ONodeAttr attr = e.getAnnotation(ONodeAttr.class);

        if (attr == null && ref != null) {
            return ref;
        }

        if (e instanceof Field) {
            return new EgggAttach(attr, ((Field) e).getName());
        } else if (e instanceof Method) {
            return new EgggAttach(attr, Property.resolvePropertyName(((Method) e).getName()));
        } else if (e instanceof Parameter) {
            return new EgggAttach(attr, ((Parameter) e).getName());
        } else {
            throw new IllegalArgumentException("Unknown element type: " + e);
        }
    }

    public static TypeWrap getTypeWrap(Type type) {
        return eggg.getTypeWrap(type);
    }
}