# snack v4.0 lab

## 特性

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
