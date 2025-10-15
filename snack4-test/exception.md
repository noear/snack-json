
java17 + 的异常序列化，需要启动一些特性。

```java
NullPointerException e = ONode.ofJson(json,
        Feature.Write_ClassName,
        Feature.Write_OnlyUseSetter,
        Feature.Write_AllowParameterizedConstructor,
        Feature.Read_OnlyUseGetter,
        Feature.Read_AutoType
).toBean(NullPointerException.class);
```