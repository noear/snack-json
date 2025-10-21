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
            .withCreatorAnnotationClass(ONodeCreator.class)
            .withAttachmentHandler(EgggUtil::doAttachmentHandle)
            .withAliasHandler(EgggAttachment::getAlias);

    private static EgggAttachment doAttachmentHandle(ClassWrap cw, AnnotatedElement e, EgggAttachment ref) {
        ONodeAttr attr = e.getAnnotation(ONodeAttr.class);

        if (attr == null && ref != null) {
            return ref;
        }

        if (e instanceof Field) {
            return new EgggAttachment(attr, ((Field) e).getName());
        } else if (e instanceof Method) {
            return new EgggAttachment(attr, Property.resolvePropertyName(((Method) e).getName()));
        } else if (e instanceof Parameter) {
            return new EgggAttachment(attr, ((Parameter) e).getName());
        } else {
            throw new IllegalArgumentException("Unknown element type: " + e);
        }
    }

    public static TypeWrap getTypeWrap(Type type) {
        return eggg.getTypeWrap(type);
    }
}