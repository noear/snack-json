
jayway jsonpath func

* https://github.com/json-path/JsonPath


| 函数名         | 处理逻辑                            | 输出类型       |
|:------------|:--------------------------------|:-----------|
| `min()`     | 提供数字数组的最小值                      | Double     |
| `max()`     | 提供数字数组的最大值                      | Double     |
| `avg()`     | 提供数字数组的平均值                      | Double     |
| `stddev()`  | 提供数字数组的标准偏差值                    | Double     |
| `length()`  | 提供数组的长度                         | Integer    |
| `sum()`     | 提供数字数组的和                        | Double     |
| `keys()`    | 提供属性键（一个终端波浪线的替代`~`）            | `Set<E>`   |
| `concat(X)` | 提供带有新项目的路径输出的连接版本               | like input |
| `append(X)` | 添加一个项目到json路径输出数组               | like input |
| `first()`   | 提供数组的第一项                        | 依赖于数组元素类型  |
| `last()`    | 提供数组的最后一项                       | 依赖于数组元素类型  |
| `index(X)`  | 提供索引为：`X` 的数组中的元素，如果X是负数，则从后往前取 | 依赖于数组元素类型  |