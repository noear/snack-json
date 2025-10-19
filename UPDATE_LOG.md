
### 4.0.2

* 调整 ONode:nodeType,getType 合并为 `type()` 与 `options()` 保持相同风格
* 调整 QueryContextImpl:multipleOf 更名为 tailafter 
* 移除 QueryContext:isInFilter （没用了）
* 优化 DescendantSegment，WildcardSelector 改为单例

### 4.0.1

* 添加 ONodeCreator 静态方法的支持（普通类）
* 添加 ONodeAttr:ignore 注解属性支持
* 添加 Write_BooleanAsNumber 新特性
* 添加 Read_UseBigDecimalMode 新特性
* 添加 Read_UseBigIntegerMode 新特性
* 添加 DecodeContext:hasFeature, EncodeContext:hasFeature 新特性
* 优化 Write_Nulls 完善对 Map 输出的控制
* 优化 Write_BrowserCompatible 写入性能


### 4.0.0

* 重构整个项目（除了名字没变，其它都变了） 
* 单测覆盖 98%，历时小半年 
* 支持 IETF JSONPath (RFC 9535) 标准（全球首个支持该标准的 Java 框架），同时兼容 `jayway.jsonpath`
* 添加 json-schema 支持

