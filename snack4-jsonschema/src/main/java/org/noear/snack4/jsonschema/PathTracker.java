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
package org.noear.snack4.jsonschema;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * JSON路径追踪器，用于记录当前JSON节点的访问路径
 */
public class PathTracker {
    private final Deque<String> stack = new ArrayDeque<>();

    /** 初始化根路径为$ */
    public PathTracker() {
        stack.push("$");
    }

    /** 进入对象属性 */
    public void enterProperty(String property) {
        String current = stack.peek();
        stack.push(current + "." + property);
    }

    /** 进入数组索引 */
    public void enterIndex(int arrayIndex) {
        String current = stack.peek();
        stack.push(current + "[" + arrayIndex + "]");
    }

    /** 获取当前路径 */
    public String currentPath() {
        return stack.peek();
    }

    /** 退出当前层级 */
    public void exit() {
        if (stack.size() > 1) {
            stack.pop();
        }
    }

    /** 创建新实例 */
    public static PathTracker begin() {
        return new PathTracker();
    }
}