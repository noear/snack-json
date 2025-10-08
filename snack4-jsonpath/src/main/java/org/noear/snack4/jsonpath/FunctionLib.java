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
import org.noear.snack4.Options;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * 函数处理库(支持动态注册)
 *
 * @author noear 2025/3/17 created
 * @since 4.0
 */
public class FunctionLib {
    private static final Map<String, BiFunction<Options, List<ONode>, ONode>> LIB = new ConcurrentHashMap<>();

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
    public static void register(String name, BiFunction<Options, List<ONode>, ONode> func) {
        LIB.put(name, func);
    }

    /**
     * 获取
     */
    public static BiFunction<Options, List<ONode>, ONode> get(String funcName) {
        return LIB.get(funcName);
    }

    /// /////////////////

    static ONode sum(Options opts, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(opts);
        }

        DoubleStream stream = nodes.stream()
                .flatMap(n -> flattenDo(n)) // 使用统一的展开方法
                .filter(ONode::isNumber)
                .mapToDouble(ONode::getDouble);

        return new ONode(opts, stream.sum());
    }


    static ONode min(Options opts, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(opts);
        }

        OptionalDouble min = collectNumbersDo(nodes).min();
        return min.isPresent() ? new ONode(opts, min.getAsDouble()) : new ONode(opts);
    }

    static ONode max(Options opts, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(opts);
        }

        OptionalDouble max = collectNumbersDo(nodes).max();
        return max.isPresent() ? new ONode(opts, max.getAsDouble()) : new ONode(opts);
    }

    static ONode avg(Options opts, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(opts);
        }

        DoubleSummaryStatistics stats = collectNumbersDo(nodes).summaryStatistics();
        return stats.getCount() > 0 ?
                new ONode(opts, stats.getAverage()) :
                new ONode(opts, null);
    }

    static ONode first(Options opts, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(opts);
        }

        return nodes.get(0);
    }

    static ONode last(Options opts, List<ONode> nodes) {
        if (nodes.isEmpty()) {
            return new ONode(opts);
        }

        return nodes.get(nodes.size() - 1);
    }

    static ONode keys(Options opts, List<ONode> nodes) {
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

    static ONode size(Options opts, List<ONode> nodes) {
        int size = nodes.stream()
                .filter(n -> n.isArray() || n.isObject())
                .mapToInt(n -> n.size())
                .sum();

        return new ONode(opts, size);
    }

    /* 字符串函数实现 */
    static ONode length(Options opts, List<ONode> nodes) {
        if (nodes.size() == 1) {
            ONode n = nodes.get(0);
            if (n.isString()) return new ONode(opts, n.getString().length());
            if (n.isArray()) return new ONode(opts, n.size());
            if (n.isObject()) return new ONode(opts, n.getObject().size());
        }
        return new ONode(opts, 0);
    }


    static ONode upper(Options opts, List<ONode> nodes) {
        return processStrings(opts, nodes, String::toUpperCase);
    }

    static ONode lower(Options opts, List<ONode> nodes) {
        return processStrings(opts, nodes, String::toLowerCase);
    }

    static ONode trim(Options opts, List<ONode> nodes) {
        return processStrings(opts, nodes, String::trim);
    }

    /// ///////////////// 工具方法 //////////////////

    private static Stream<ONode> flatten(Options opts, ONode node) {
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

    private static ONode processStrings(Options opts, List<ONode> nodes, Function<String, String> processor) {
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
                new ONode(opts, results.get(0)) :
                ONode.ofBean(results);
    }
}