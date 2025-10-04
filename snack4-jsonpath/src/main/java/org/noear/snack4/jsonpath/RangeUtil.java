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

/**
 * @author noear 2025/5/5 created
 */
public class RangeUtil {
    public static int normalize(int val, int size) {
        if (val >= 0) {
            return val;
        } else {
            return size + val;
        }
    }

    public static Bounds bounds(int start, int end, int step, int size) {
        int n_start = start;//normalize(start, size);
        int n_end = end;//normalize(end, size);

        int lower, upper;
        if (step >= 0) {
            lower = Math.min(Math.max(n_start, 0), size);
            upper = Math.min(Math.max(n_end, 0), size);
        } else {
            upper = Math.min(Math.max(n_start, -1), size - 1);
            lower = Math.min(Math.max(n_end, -1), size - 1);
        }

        return new Bounds(lower, upper);
    }

    public static class Bounds {
        final int lower, upper;

        public Bounds(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }

        public int getLower() {
            return lower;
        }

        public int getUpper() {
            return upper;
        }
    }
}