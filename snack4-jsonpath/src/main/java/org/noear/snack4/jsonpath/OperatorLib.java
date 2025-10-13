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
package org.noear.snack4.jsonpath;


import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.filter.Term;
import org.noear.snack4.jsonpath.operator.*;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 操作符库（支持动态注册）
 *
 * @author noear 2025/5/5 created
 * @since 4.0
 */
public class OperatorLib {
    private static final Map<String, Operator> LIB = new ConcurrentHashMap<>();

    static {
        //协议规定
        register("==", new CompareOperator(CompareType.EQ));
        register("!=", new CompareOperator(CompareType.NEQ));
        register(">", new CompareOperator(CompareType.GT));
        register(">=", new CompareOperator(CompareType.GTE));
        register("<", new CompareOperator(CompareType.LT));
        register("<=", new CompareOperator(CompareType.LTE));

        //扩展
        register("=~", new MatchesOperator());

        register("in", new InOperator());
        register("nin", new NinOperator());

        register("subsetof", new SubsetofOperator());

        register("anyof", new AnyofOperator());
        register("noneof", new NoneofOperator());

        register("size", new SizeOperator());
        register("empty", new EmptyOperator());

        register("startsWith", new StartsWithOperator());
        register("endsWith", new EndsWithOperator());
        register("contains", new ContainsOperator());
    }

    /**
     * 注册
     */
    public static void register(String name, Operator func) {
        LIB.put(name, func);
    }

    /**
     * 获取
     */
    public static Operator get(String funcName) {
        return LIB.get(funcName);
    }
}