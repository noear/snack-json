<h1 align="center" style="text-align:center;">
  Snack
</h1>
<p align="center">
	<strong>一个 Json Dom & JsonPath 的框架（for Java）</strong>
</p>
<p align="center">
	同时兼容 `jayway.jsonpath` 和 <a href="https://www.rfc-editor.org/rfc/rfc9535.html" target="_blank">IETF JSONPath (RFC 9535)</a> 标准
</p>
<p align="center">
    <a target="_blank" href="https://central.sonatype.com/artifact/org.noear/snack4">
        <img src="https://img.shields.io/maven-central/v/org.noear/snack4.svg?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8-green.svg" alt="jdk-8" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-11-green.svg" alt="jdk-11" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-17-green.svg" alt="jdk-17" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-21-green.svg" alt="jdk-21" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/downloads/">
		<img src="https://img.shields.io/badge/JDK-25-green.svg" alt="jdk-25" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/noear/snack-jsonpath/stargazers'>
        <img src='https://gitee.com/noear/snack-jsonpath/badge/star.svg' alt='gitee star'/>
    </a>
    <a target="_blank" href='https://github.com/noear/snack-jsonpath/stargazers'>
        <img src="https://img.shields.io/github/stars/noear/snack-jsonpath.svg?style=flat&logo=github" alt="github star"/>
    </a>
</p>

<hr />

##### 语言： 中文 | [English](README.md) 

<hr />

基于jdk8。支持：Json Dom 的构建、编码解转换、获取、JsonPath 查询、JsonSchema 验证。

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack4-jsonpath</artifactId>
  <version>4.0.0-SNAPSHOT</version>
</dependency>
```

Snack-Jsonpath 借鉴了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。其下一切数据都以`ONode`表示，`ONode`也即 `One node` 之意，代表任何类型，也可以转换为任何类型。

* 强调文档树的构建和操控能力
* 高性能`Json path`查询（比 jayway.jsonpath 快很多），同时兼容 `jayway.jsonpath` 和 [IETF JSONPath (RFC 9535) 标准](https://www.rfc-editor.org/rfc/rfc9535.html) (用 `options` 切换)
* 支持 `Json schema` 架构校验
* 优先使用 无参构造函数 + 字段 编解码（可减少注入而触发动作的风险）


| 依赖包                           | 描述                       |  
|-------------------------------|--------------------------| 
| `org.noear:snack4`            | 提供 json `dom` 构建与编解码基础支持 |   
| `org.noear:snack4-jsonpath`   | 提供 `json path` 查询支持      |   
| `org.noear:snack4-jsonschema` | 提供 `json schema` 校验支持    |  


### JSONPath 语法参考（[IETF JSONPath (RFC 9535)]((https://www.rfc-editor.org/rfc/rfc9535.html))）

| 语法元素              | 描述                           |
|-------------------|------------------------------|
| `$`               | 根节点标识符                       |
| `@`               | 当前节点标识符（仅在过滤选择器中有效）          |
| `[<selectors>]`   | 子段：选择节点的零个或多个子节点             |
| `.name`           | 简写 `['name']`                |
| `.*`              | 简写 `[*]`                     |
| `..[<selectors>]` | 后代段：选择节点的零个或多个后代             |
| `..name`          | 简写 `..['name']`              |
| `..*`             | 简写 `..[*]`                   |
| `'name'`          | 名称选择器：选择对象的命名子对象             |
| `*`               | 通配符选择器：选择节点的所有子节点            |
| `3`               | 索引选择器：选择数组的索引子项（从 0 开始）      |
| `0:100:5`         | 数组切片选择器：数组的 `start:end:step` |
| `?<logical-expr>` | 过滤选择器：使用逻辑表达式选择特定的子项         |
| `fun(@.foo)`      | 过滤函数扩展：在过滤表达式中调用函数           |
| `.fun()`          | 聚合函数                         |


过滤选择器语法参考：

| 语法                          | 描述       | 优先级 |
|-----------------------------|----------|-----|
| `(...)`                     | 分组       | 5   |
| `name(...)`                 | 函数扩展     | 5   |
| `!`                         | 逻辑 `非`   | 4   |
| `==`,`!=`,`<`,`<=`,`>`,`>=` | 关系比较符    | 3   |
| `&&`                        | 逻辑 `与`   | 2   |
| `\|\|`                      | 逻辑 `或`   | 1   |


### 放几个示例

支持 `dom` 操控

```java
ONode oNode = new ONode();
oNode.set("id", 1);
oNode.getOrNew("layout").then(o -> {
    o.addNew().set("title", "开始").set("type", "start");
    o.addNew().set("title", "结束").set("type", "end");
});

oNode.get("id").getInt();
oNode.get("layout").get(0).get("title").getString();

oNode.getOrNew("list").fillJson("[1,2,3,4,5,6]");
```


支持 `json path` 查询、构建、删除

```java
ONode.ofBean(store).select("$..book[?@.tags contains 'war'].first()").toBean(Book.class); //RFC9535 规范，可以没有括号
ONode.ofBean(store).select("$..book[?(!(@.category == 'fiction') && @.price < 40)].first()").toBean(Book.class);
ONode.ofJson(store).select("$.store.book.count()");

ONode.ofBean(store).create("$.store.book[0].category").toJson();

ONode.ofBean(store).delete("$..book[-1]");
```


支持 `json schema` 校验

```java
JsonSchema schema = JsonSchema.ofJson("{type:'object',properties:{userId:{type:'string'}}}"); //加载架构定义

schema.validate(ONode.load("{userId:'1'}")); //校验格式
```


支持序列化、反序列化

```java
User user = new User();
ONode.ofBean(user).toBean(User.class); //可以作为 bean 转换使用
ONode.ofBean(user).toJson();

ONode.ofJson("{}").toBean(User.class);
ONode.ofJson("[{},{}]").toBean((new ArrayList<User>(){}).getClass());

//快捷方式
String json = ONode.serialize(user);
User user = ONode.deserialize(json, User.class);
```

### 路径树接口

```java
ONode o = ONode.ofJson(json).usePaths(); //会为每个子节点，生成 path 属性

ONode rst = o.select("$.data.list[*].mobile");
List<String> rstPaths = rst.pathList(); //获取结果节点的路径列表
for(ONode n1 : rst.getArray()) {
   n1.path(); //当前路径
   n1.parent(); //父级节点
}

ONode rst = o.get("data").get("list").get(2);
rst.path();
rst.parent();
```



### 高级定制


```java
Options options = Options.of();
//添加编码器
options.addEncoder(Date.class, (ctx, value, target) -> {
    target.setValue(DateUtil.format(data, "yyyy-MM-dd"));
});
//添加解码器
options.addDecoder(Date.class, ...);
options.addFactory(...);

//添加特性
options.addFeature(Feature.Write_PrettyFormat);

//移除特性
options.removeFeature(Feature.Write_PrettyFormat);

//设置日期格式附
options.addFeature(Feature.Write_UseDateFormat); //使用日期格式
options.dateFormat("yyyy-MM");

//..

String json = ONode.ofBean(orderModel, options).toJson();
```

