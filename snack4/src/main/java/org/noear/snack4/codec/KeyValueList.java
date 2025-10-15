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
package org.noear.snack4.codec;

import java.util.*;

/**
 * 键值对列表
 *
 * @author noear 2025/10/15 created
 * @since 4.0
 */
public class KeyValueList implements Iterable<Map.Entry<String, String>> {
    private final List<Map.Entry<String, String>> entryList = new ArrayList<>();

    public int size() {
        return entryList.size();
    }

    public void sort() {
        entryList.sort(Map.Entry.comparingByKey());
    }

    public void add(String name, String value) {
        entryList.add(new AbstractMap.SimpleEntry<>(name, value));
    }

    public Map.Entry<String, String> get(int index) {
        return entryList.get(index);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return entryList.iterator();
    }
}
