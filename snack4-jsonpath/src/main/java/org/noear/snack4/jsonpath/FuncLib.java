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
import org.noear.snack4.jsonpath.func.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展函数库（支持动态注册）
 *
 * @author noear 2025/3/17 created
 * @since 4.0
 */
public class FuncLib {
    private static final Map<String, Func> LIB = new ConcurrentHashMap<>();

    static {
        //:: for jayway
        register("min", new MinFunc());
        register("max", new MaxFunc());
        register("avg", new AvgFunc());
        register("stddev", FuncLib::stddev);
        //length
        register("size", new LengthFunc());

        register("sum", FuncLib::sum);
        register("keys", new KeysFunc());

        register("concat", new ConcatFunc());
        register("append", new AppendFunc());

        register("first", new FirstFunc());
        register("last", new LastFunc());
        register("index", new IndexFunc());


        //for rfc9535
        register("length", new LengthFunc());
        register("count", new CountFunc());
        register("match", new MatchFunc());
        register("search", new SearchFunc());
        register("value", new ValueFunc());
    }

    /**
     * 注册
     */
    public static void register(String name, Func func) {
        LIB.put(name, func);
    }

    /**
     * 获取
     */
    public static Func get(String funcName) {
        return LIB.get(funcName);
    }

    /// /////////////////

    static ONode sum(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        Double _sum = MathUtil.sum(oNodes);

        return new ONode(ctx.getOptions(), _sum);
    }

    static ONode stddev(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        List<Double> doubleList = MathUtil.getDoubleList(oNodes);

        Double ref = MathUtil.calculateStdDev(doubleList);

        return new ONode(ctx.getOptions(), ref);
    }
}