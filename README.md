<h1 align="center" style="text-align:center;">
  Snack-Json for Java
</h1>
<p align="center">
	<strong>一个高性能的 JsonPath 框架</strong>
</p>

<p align="center">
    <a target="_blank" href="https://search.maven.org/artifact/org.noear/snack3">
        <img src="https://img.shields.io/maven-central/v/org.noear/snack3.svg?label=Maven%20Central" alt="Maven" />
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
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk23-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-23-green.svg" alt="jdk-23" />
	</a>
    <br />
    <a target="_blank" href='https://gitee.com/noear/snack3/stargazers'>
        <img src='https://gitee.com/noear/snack3/badge/star.svg' alt='gitee star'/>
    </a>
    <a target="_blank" href='https://github.com/noear/snack3/stargazers'>
        <img src="https://img.shields.io/github/stars/noear/snack3.svg?style=flat&logo=github" alt="github star"/>
    </a>
</p>

<br/>
<p align="center">
	<a href="https://jq.qq.com/?_wv=1027&k=kjB5JNiC">
	<img src="https://img.shields.io/badge/QQ交流群-22200020-orange"/></a>
</p>


<hr />

基于jdk8。支持：序列化反序列化、解析和转换、构建、查找、Json path 查询。

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack4</artifactId>
  <version>4.0.0-SNAPSHOT</version>
</dependency>
```

Snack-Json 借鉴了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。其下一切数据都以`ONode`表示，`ONode`也即 `One node` 之意，代表任何类型，也可以转换为任何类型。
* 强调文档树的操控和构建能力
* 高性能`Json path`查询（兼容性和性能很赞）
* 可以 `Json Schema` 架构校验
* 顺带支持`序列化、反序列化`
* 基于 无参构造函数 + 字段 操作实现（因注入而触发动作的风险，不会有）


| 依赖包                           | 描述                   |  
|-------------------------------|----------------------| 
| `org.noear:snack4`            | 提供基础能力：解析、构建、序列化反序列化 |   
| `org.noear:snack4-jsonpath`   | 提供 jsonpath 扩展支持     |   
| `org.noear:snack4-jsonschema` | 提供 jsonschema 扩展支持   |  


## 放几个示例

支持 dom 操控

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

支持 jsonpath 查询、构建、删除

```java
ONode.ofBean(store).select("$..book[?@.tags contains 'war'].first()").toBean(Book.class); //RFC9535 规范，可以没有括号
ONode.ofBean(store).select("$..book[?(!(@.category == 'fiction') && @.price < 40)].first()").toBean(Book.class);
ONode.ofJson(store).select("$.store.book.count()");

ONode.ofBean(store).create("$.store.book[0].category").toJson();

ONode.ofBean(store).delete("$..book[-1]");
```


支持架构校验

```java
JsonSchema schema = JsonSchema.ofJson("{type:'object',properties:{userId:{type:'string'}}}"); //加载架构定义

schema.validate(ONode.load("{userId:'1'}")); //校验格式
```


## 路径树接口

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



## 高级定制


```java
Options options = Options.of();
//添加编码器
options.addEncoder(Date.class, (ctx, value, target) -> {
    target.setValue(DateUtil.format(data, "yyyy-MM-dd"));
});
//添加解码器
options.addDecoder(Date.class, ...);

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

