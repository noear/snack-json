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

import org.noear.snack4.annotation.ONodeCreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author noear 2025/10/15 created
 * @since 4.0
 */
public class ConstrWrap {
    private final Constructor<?> constr;

    private final Map<String, ParamWrap> paramNodeWraps;
    private final List<ParamWrap> paramAry;

    private final boolean security;

    public ConstrWrap(TypeWrap owner, Constructor<?> constr, ONodeCreator constrAnno) {
        this.constr = constr;

        paramNodeWraps = new HashMap<>();
        paramAry = new ArrayList<>();

        for (Parameter p1 : constr.getParameters()) {
            ParamWrap paramWrap = new ParamWrap(owner, p1);

            paramNodeWraps.put(paramWrap.getNodeName(), paramWrap);
            paramAry.add(paramWrap);
        }

        security = (constr.getParameterCount() == 0 || constrAnno != null || ClassUtil.isRecordClass(owner.getType()));
    }

    /**
     * 是否安全（无参数或有注解）
     */
    public boolean isSecurity() {
        return security;
    }

    public List<ParamWrap> getParamAry() {
        return paramAry;
    }

    public int getParamCount() {
        return paramAry.size();
    }

    public boolean hasParam(String nodeName) {
        return paramNodeWraps.containsKey(nodeName);
    }

    public <T> T newInstance(Object... args)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (constr.isAccessible() == false) {
            constr.setAccessible(true);
        }

        return (T) constr.newInstance(args);
    }
}