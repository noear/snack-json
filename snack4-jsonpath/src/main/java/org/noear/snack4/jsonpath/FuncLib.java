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
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * JsonPath 函数处理库(支持动态注册)
 *
 * @author noear 2025/3/17 created
 * @since 4.0
 */
public class FuncLib {
    private static final Map<String, Func> LIB = new ConcurrentHashMap<>();

    static {
        //:: for jayway
        register("min", FuncLib::min);
        register("max", FuncLib::max);
        register("avg", FuncLib::avg);
        register("stddev", FuncLib::stddev);
        //length
        register("size", new LengthFunc());

        register("sum", FuncLib::sum);
        register("keys", new KeysFunc());

        register("concat", new ConcatFun());
        register("append", new AppendFun());

        register("first", new FirstFunc());
        register("last", new LastFunc());
        register("index", new IndexFunc());


        //for rfc9535
        register("length", new LengthFunc());
        register("count", new CountFunc());
        register("match", new MatchFunc());
        register("search", new SearchFunc());
        register("value", new ValueFunc());


        // 字符串函数
        //register("upper", FuncLib::upper);
        //register("lower", FuncLib::lower);
        //register("trim", FuncLib::trim);
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

    static ONode avg(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        Double _avg = MathUtil.avg(oNodes);

        return new ONode(ctx.getOptions(), _avg);
    }

    static ONode min(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        Double ref = null;
        for (ONode n : oNodes) {
            if (n.isArray()) {
                for (ONode o : n.getArray()) {
                    if (o.isNumber()) {
                        if(ref == null){
                            ref = o.getDouble();
                        } else {
                            if (ref > o.getDouble()) {
                                ref = o.getDouble();
                            }
                        }
                    }
                }
            } else if (n.isNumber()) {
                if(ref == null){
                    ref = n.getDouble();
                } else {
                    if (ref > n.getDouble()) {
                        ref = n.getDouble();
                    }
                }
            }
        }

        return new ONode(ctx.getOptions(), ref);
    }

    static ONode max(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        Double ref = null;
        for (ONode n : oNodes) {
            if (n.isArray()) {
                for (ONode o : n.getArray()) {
                    if (o.isNumber()) {
                        if (ref == null) {
                            ref = o.getDouble();
                        } else {
                            if (ref < o.getDouble()) {
                                ref = o.getDouble();
                            }
                        }
                    }
                }
            } else if (n.isNumber()) {
                if (ref == null) {
                    ref = n.getDouble();
                } else {
                    if (ref < n.getDouble()) {
                        ref = n.getDouble();
                    }
                }
            }
        }

        return new ONode(ctx.getOptions(), ref);
    }

    static ONode stddev(QueryContext ctx, List<ONode> oNodes) {
        if (oNodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        List<Double> doubleList = MathUtil.getDoubleList(oNodes);

        Double ref = MathUtil.calculateStdDev(doubleList);

        return new ONode(ctx.getOptions(), ref);
    }

    static ONode upper(QueryContext ctx, List<ONode> oNodes) {
        return processStrings(ctx, oNodes, String::toUpperCase);
    }

    static ONode lower(QueryContext ctx, List<ONode> oNodes) {
        return processStrings(ctx, oNodes, String::toLowerCase);
    }

    static ONode trim(QueryContext ctx, List<ONode> oNodes) {
        return processStrings(ctx, oNodes, String::trim);
    }

    /// ///////////////// 工具方法 //////////////////

    private static Stream<ONode> flatten(QueryContext ctx, ONode node) {
        return flattenDo(node);
    }

    private static Stream<ONode> flattenDo(ONode node) {
        if (node.isArray()) {
            return node.getArray().stream().flatMap(FuncLib::flattenDo);
        } else {
            return Stream.of(node);
        }
    }

    private static DoubleStream collectNumbersDo(List<ONode> oNodes) {
        return oNodes.stream()
                .flatMap(n -> n.isArray() ?
                        n.getArray().stream() :
                        Stream.of(n))
                .filter(ONode::isNumber)
                .mapToDouble(ONode::getDouble);
    }

    private static ONode processStrings(QueryContext ctx, List<ONode> oNodes, java.util.function.Function<String, String> processor) {
        List<String> results = oNodes.stream()
                .flatMap(n -> {
                    if (n.isString()) {
                        return Stream.of(n.getString());
                    } else if (n.isArray()) {
                        return n.getArray().stream()
                                .filter(ONode::isString)
                                .map(ONode::getString);
                    }
                    return Stream.empty();
                })
                .map(processor)
                .collect(Collectors.toList());

        return results.size() == 1 ?
                new ONode(ctx.getOptions(), results.get(0)) :
                ONode.ofBean(results);
    }
}