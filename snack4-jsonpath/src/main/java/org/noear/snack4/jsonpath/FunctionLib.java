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

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
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
public class FunctionLib {
    private static final Map<String, Function> LIB = new ConcurrentHashMap<>();

    static {
        // 聚合函数
        register("min", FunctionLib::min);
        register("max", FunctionLib::max);
        register("avg", FunctionLib::avg);
        register("sum", FunctionLib::sum);

        // 集合函数
        register("size", FunctionLib::size);
        register("keys", FunctionLib::keys);
        register("first", FunctionLib::first);
        register("last", FunctionLib::last);

        // 字符串函数
        register("length", FunctionLib::length);
        register("upper", FunctionLib::upper);
        register("lower", FunctionLib::lower);
        register("trim", FunctionLib::trim);
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

    /// /////////////////

    static ONode sum(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        DoubleStream stream = nodes.stream()
                .flatMap(n -> flattenDo(n)) // 使用统一的展开方法
                .filter(ONode::isNumber)
                .mapToDouble(ONode::getDouble);

        return new ONode(ctx.getOptions(), stream.sum());
    }


    static ONode min(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        Double ref = null;
        for (ONode n : nodes) {
            if (n.isArray()) {
                for (ONode n1 : n.getArray()) {
                    if (n1.isNumber()) {
                        if (ref == null) {
                            ref = n1.getDouble();
                        } else {
                            if (ref > n1.getDouble()) {
                                ref = n1.getDouble();
                            }
                        }
                    }
                }
            } else if (n.isNumber()) {
                if (ref == null) {
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

        OptionalDouble max = collectNumbersDo(nodes).max();
        return max.isPresent() ? new ONode(ctx.getOptions(), max.getAsDouble()) : new ONode(ctx.getOptions());
    }

    static ONode avg(QueryContext ctx, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(ctx.getOptions());
        }

        DoubleSummaryStatistics stats = collectNumbersDo(nodes).summaryStatistics();
        return stats.getCount() > 0 ?
                new ONode(ctx.getOptions(), stats.getAverage()) :
                new ONode(ctx.getOptions(), null);
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
        if (nodes.size() == 1) {
            ONode node = nodes.get(0);

            if (node.isObject()) {
                return ONode.ofBean(node.getObject().keySet());
            } else {
                throw new JsonPathException("keys() requires object");
            }
        } else {
            throw new JsonPathException("keys() requires object");
        }
    }

    static ONode size(QueryContext ctx, List<ONode> nodes) {
        int size = nodes.stream()
                .filter(n -> n.isArray() || n.isObject())
                .mapToInt(n -> n.size())
                .sum();

        return new ONode(ctx.getOptions(), size);
    }

    /* 字符串函数实现 */
    static ONode length(QueryContext ctx, List<ONode> nodes) {
        if (nodes.size() == 1) {
            ONode n = nodes.get(0);
            if (n.isString()) return new ONode(ctx.getOptions(), n.getString().length());
            if (n.isArray()) return new ONode(ctx.getOptions(), n.size());
            if (n.isObject()) return new ONode(ctx.getOptions(), n.getObject().size());
        }
        return new ONode(ctx.getOptions(), 0);
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
            return node.getArray().stream().flatMap(FunctionLib::flattenDo);
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