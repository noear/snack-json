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

import org.noear.snack4.jsonpath.function.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展函数库（支持动态注册）
 *
 * @author noear 2025/3/17 created
 * @since 4.0
 */
public class FunctionLib {
    private static final Map<String, Function> LIB = new ConcurrentHashMap<>();

    static {
        //:: for jayway
        register("min", new MinFunction());
        register("max", new MaxFunction());
        register("avg", new AvgFunction());
        register("stddev", new StddevFunction());
        //length
        register("size", new LengthFunction());

        register("sum", new SumFunction());
        register("keys", new KeysFunction());

        register("concat", new ConcatFunction());
        register("append", new AppendFunction());

        register("first", new FirstFunction());
        register("last", new LastFunction());
        register("index", new IndexFunction());


        //for rfc9535
        register("length", new LengthFunction());
        register("count", new CountFunction());
        register("match", new MatchFunction());
        register("search", new SearchFunction());
        register("value", new ValueFunction());
    }

    /**
     * 注册
     */
    public static void register(String name, Function func) {
        LIB.put(name, func);
    }

    /**
     * 获取
     */
    public static Function get(String funcName) {
        return LIB.get(funcName);
    }
}