<h1 align="center" style="text-align:center;">
  Snack
</h1>
<p align="center">
	<strong>A Json Dom & JsonPath Framework (for Java)</strong>
</p>
<p align="center">
	Compatible with `jayway.jsonpath` and <a href="https://www.rfc-editor.org/rfc/rfc9535.html" target="_blank">IETF JSONPath (RFC 9535)</a> standards
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

##### Language: English | [中文](README_CN.md) 

<hr />

jdk8 based. Support: Json Dom construction, encoding and decoding, fetching, JsonPath query, JsonSchema validation.

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack4-jsonpath</artifactId>
  <version>4.0.0-SNAPSHOT</version>
</dependency>
```

Snach-jsonpath draws on the design of `Javascript` where all variables are declared with `var` and `Xml dom` where everything is `Node`. Everything underneath it is represented by an `ONode`, which stands for `One node` and can be converted to any type.

* It emphasizes the ability to build and manipulate the document tree
* High performance `Json path` queries (much faster than `jayway.jsonpath`), Compatible with `jayway.jsonpath` and [IETF JSONPath (RFC 9535)](https://www.rfc-editor.org/rfc/rfc9535.html) standards (Switch with `optoins`)
* Supports `Json Schema` validation
* Prefer no-argument constructors + field codec (reduces the risk of triggering actions by injection)


| dependencies                        | description                                            |  
|-------------------------------------|--------------------------------------------------------| 
| `org.noear:snack4`                  | Provides basic support for `dom` construction and codec |   
| `org.noear:snack4-jsonpath`         | Provides `json path` query support                     |   
| `org.noear:snack4-jsonschema`       | Provides `json schema` validation support              |  


### JSONPath syntax reference([IETF JSONPath (RFC 9535)]((https://www.rfc-editor.org/rfc/rfc9535.html)))

| Syntax Element    | Description                                                             |
|-------------------|-------------------------------------------------------------------------|
| `$`               | root node identifier                                                    |
| `@`               | current node identifier (valid only within filter selectors)            |
| `[<selectors>]`   | 	child segment: selects zero or more children of a node                 |
| `.name`           | shorthand for `['name']`                                                |
| `.*`              | shorthand for `[*]`                                                     |
| `..[<selectors>]` | descendant segment: selects zero or more descendants of a node          |
| `..name`          | shorthand for `..['name']`                                              |
| `..*`             | shorthand for `..[*]`                                                   |
| `'name'`          | name selector: selects a named child of an object                       |
| `*`               | wildcard selector: selects all children of a node                       |
| `3`               | index selector: selects an indexed child of an array (from 0)           |
| `0:100:5`         | array slice selector: `start:end:step` for arrays                       |
| `?<logical-expr>` | filter selector: selects particular children using a logical expression |
| `fun(@.foo)`      | filter function extension: invokes a function in a filter expression    |
| `.fun()`          | aggregate function                                                      |

Filter selector syntax reference:

| Syntax                       | Description            | Precedence |
|------------------------------|------------------------|------------|
| `(...)`                      | Grouping               | 5          |
| `name(...)`                  | Function Expressions   | 5          |
| `!`                          | Logical NOT	           | 4          |
| `==`,`!=`,`<`,`<=`,`>`,`>=`  | Relations              | 3          |
| `&&`                         | Logical AND            | 2          |
| `\|\|`                       | Logical OR	            | 1          |



### examples

Support `dom` manipulation

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


Supports `json path` query, build, and delete

```java
ONode.ofBean(store).select("$..book[?@.tags contains 'war'].first()").toBean(Book.class); //RFC9535 规范，可以没有括号
ONode.ofBean(store).select("$..book[?(!(@.category == 'fiction') && @.price < 40)].first()").toBean(Book.class);
ONode.ofJson(store).select("$.store.book.count()");

ONode.ofBean(store).create("$.store.book[0].category").toJson();

ONode.ofBean(store).delete("$..book[-1]");
```


Supports `json schema` validation

```java
JsonSchema schema = JsonSchema.ofJson("{type:'object',properties:{userId:{type:'string'}}}"); //加载架构定义

schema.validate(ONode.load("{userId:'1'}")); //校验格式
```


Supports serialization and deserialization

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

### Path tree interface

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



### Advanced customization


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

