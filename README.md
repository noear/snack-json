# snack v4.0 lab

基于jdk8。支持：序列化反序列化、解析和转换、构建、查找、Json path 查询。

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack4</artifactId>
  <version>4.0.0-SNAPSHOT</version>
</dependency>
```

Snack4 借鉴了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。其下一切数据都以`ONode`表示，`ONode`也即 `One node` 之意，代表任何类型，也可以转换为任何类型。
* 强调文档树的操控和构建能力
* 高性能`Json path`查询（兼容性和性能很赞；暂不支持多条件）
* 顺带支持`序列化、反序列化`
* 基于 无参构造函数 + 字段 操作实现（因注入而触发动作的风险，不会有）


## 放几个示例

支持 dom 操控

```java
ONode oNode = new ONode();
oNode.set("id", 1);
oNode.getOrNew("layout").then(o -> {
    o.addNew().set("title", "开始").set("type", "start");
    o.addNew().set("title", "结束").set("type", "end");
});
```

支持序列化、反序列化

```java
User user = new User();
ONode.from(user).toBean(User.class);
ONode.from(user).toJson();

ONode.load("{}").toBean(User.class);
ONode.load("[{},{}]").toBean((new ArrayList<User>(){}).getClass());
```

支持 jsonpath 查询、构建、删除

```java
ONode.from(store).select("$..book[?@.tags contains 'war']").toBean(Book.class); //RFC9535 规范，可以没有括号
ONode.from(store).select("$..book[?(!(@.category == 'fiction') && @.price < 40)]").toBean(Book.class);
ONode.load(store).select("$.store.book.count()");

ONode.from(store).create("$.store.book[0].category").toJson();

ONode.from(store).delete("$..book[-1]");
```

支持架构校验

```java
JsonSchema schema = JsonSchema.load("{type:'object',properties:{userId:{type:'string'}}}"); //加载架构定义

schema.validate(ONode.load("{userId:'1'}")); //校验格式
```

## 高级定制

