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
package org.noear.snack4;

/**
 * 技术规范
 *
 * @author noear 2025/10/11 created
 * @since 4.0
 */
public enum Standard {
    JSONPath_Jayway,
    JSONPath_IETF_RFC_9535,
    ;

    private final int _mask;

    Standard() {
        _mask = (1 << ordinal());
    }

    public int mask() {
        return _mask;
    }

    public static int addStandard(int ref, Standard... standards) {
        for (Standard feature : standards) {
            ref |= feature.mask();
        }
        return ref;
    }

    public static int removeStandard(int ref, Standard... standards) {
        for (Standard feature : standards) {
            ref &= ~feature.mask();
        }
        return ref;
    }

    public static boolean hasStandard(int ref, Standard standard) {
        return (ref & standard.mask()) != 0;
    }
}
