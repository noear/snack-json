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

import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.Parameter;

/**
 *
 * @author noear 2025/10/15 created
 * @since 4.0
 */
public class ParamWrap {
    private final Parameter param;
    private final TypeWrap paramTypeWrap;

    private final ONodeAttrHolder attr;
    private final String name;
    private final String nodeName;

    public ParamWrap(TypeWrap owner, Parameter param) {
        this.param = param;
        this.paramTypeWrap = TypeWrap.from(GenericUtil.reviewType(param.getParameterizedType(), owner.getGenericInfo()));

        this.name = param.getName();

        String nodeNameTmp = null;
        ONodeAttr attrAnno = param.getAnnotation(ONodeAttr.class);
        if (attrAnno != null) {
            nodeNameTmp = attrAnno.name();
            this.attr = new ONodeAttrHolder(attrAnno, false);
        } else {
            this.attr = new ONodeAttrHolder(null, false);
        }

        if (Asserts.isEmpty(nodeNameTmp)) {
            nodeName = this.name;
        } else {
            nodeName = nodeNameTmp;
        }
    }

    public Parameter getParam() {
        return param;
    }

    public TypeWrap getTypeWrap() {
        return paramTypeWrap;
    }

    public ONodeAttrHolder getAttr() {
        return attr;
    }

    public String getOrigName() {
        return name;
    }

    public String getNodeName() {
        return nodeName;
    }
}