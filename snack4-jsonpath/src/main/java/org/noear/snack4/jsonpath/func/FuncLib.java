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
package org.noear.snack4.jsonpath.func;

import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.JsonPathException;
import org.noear.snack4.jsonpath.QueryContext;

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
        // 聚合函数
        register("min", FuncLib::min);
        register("max", FuncLib::max);
        register("avg", FuncLib::avg);
        register("sum", FuncLib::sum);

        // 集合函数
        register("size", FuncLib::size);
        register("keys", FuncLib::keys);
        register("first", FuncLib::first);
        register("last", FuncLib::last);

        // 字符串函数
        register("upper", FuncLib::upper);
        register("lower", FuncLib::lower);
        register("trim", FuncLib::trim);

        //过滤函数
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

    static ONode sum(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        double ref = 0D;
        int count = 0;
        for (ONode n : nodes) {
            if (n.isArray()) {
                for (ONode o : n.getArray()) {
                    if (o.isNumber()) {
                        ref += o.getDouble();
                        count++;
                    }
                }
            } else if (n.isNumber()) {
                ref += n.getDouble();
                count++;
            }
        }

        if (count == 0) {
            return new ONode(ctx.getOptions());
        } else {
            return new ONode(ctx.getOptions(), ref);
        }
    }

    static ONode avg(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        double ref = 0D;
        int count = 0;
        for (ONode n : nodes) {
            if (n.isArray()) {
                for (ONode o : n.getArray()) {
                    if (o.isNumber()) {
                        ref += o.getDouble();
                        count++;
                    }
                }
            } else if (n.isNumber()) {
                ref += n.getDouble();
                count++;
            }
        }

        if (count == 0) {
            return new ONode(ctx.getOptions());
        } else {
            return new ONode(ctx.getOptions(), ref / count);
        }
    }

    static ONode min(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        Double ref = null;
        for (ONode n : nodes) {
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

    static ONode max(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        Double ref = null;
        for (ONode n : nodes) {
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

    static ONode first(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        return nodes.get(0);
    }

    static ONode last(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        return nodes.get(nodes.size() - 1);
    }

    static ONode keys(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        if (nodes.size() > 1) {
            Set<String> results = new HashSet<>();
            for (ONode n1 : nodes) {
                if (n1.isObject() && n1.getObject().size() > 0) {
                    results.addAll(n1.getObject().keySet());
                }
            }

            if (results.size() > 0) {
                return new ONode(ctx.getOptions()).addAll(results);
            }
        } else {
            ONode n1 = nodes.get(0);

            if (n1.isObject() && n1.getObject().size() > 0) {
                return ONode.ofBean(n1.getObject().keySet());
            }
        }

        return new ONode(ctx.getOptions());
    }

    static ONode size(QueryContext ctx, List<ONode> nodes) {
        int size = nodes.stream()
                .filter(n -> n.isArray() || n.isObject())
                .mapToInt(n -> n.size())
                .sum();

        return new ONode(ctx.getOptions(), size);
    }


    static ONode upper(QueryContext ctx, List<ONode> nodes) {
        return processStrings(ctx, nodes, String::toUpperCase);
    }

    static ONode lower(QueryContext ctx, List<ONode> nodes) {
        return processStrings(ctx, nodes, String::toLowerCase);
    }

    static ONode trim(QueryContext ctx, List<ONode> nodes) {
        return processStrings(ctx, nodes, String::trim);
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

    private static DoubleStream collectNumbersDo(List<ONode> nodes) {
        return nodes.stream()
                .flatMap(n -> n.isArray() ?
                        n.getArray().stream() :
                        Stream.of(n))
                .filter(ONode::isNumber)
                .mapToDouble(ONode::getDouble);
    }

    private static ONode processStrings(QueryContext ctx, List<ONode> nodes, java.util.function.Function<String, String> processor) {
        List<String> results = nodes.stream()
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