package org.noear.snack;

import org.noear.snack.core.*;
import org.noear.snack.from.Fromer;
import org.noear.snack.to.Toer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 节点（One Node）
 *
 * @author noear
 * */
public class ONode {
    //内部配置
    protected Options _o;
    //内部数据
    protected ONodeData _d;
    //父节点
    protected ONode _p;

    /**
     * @return 版本信息
     */
    public static String version() {
        return "3.2";
    }

    public ONode() {
        this(null, null);
    }

    public ONode(Options options) {
        this(null, options);
    }

    public ONode(ONode parent, Options options) {
        _p = parent;
        _d = new ONodeData(this);

        if (options == null) {
            _o = Options.def();
        } else {
            _o = options;
        }
    }

    @Deprecated
    public static ONode newValue() {
        return new ONode().asValue();
    }

    @Deprecated
    public static ONode newObject() {
        return new ONode().asObject();
    }

    @Deprecated
    public static ONode newArray() {
        return new ONode().asArray();
    }

    /**
     * 父节点（可能为 null）
     * */
    public ONode parent(){
        return _p;
    }

    /**
     * 深度父节点（可能为 null）
     * */
    public ONode parents(int depth) {
        ONode tmp = _p;
        while (depth > 0) {
            if (tmp == null) {
                break;
            } else {
                tmp = tmp.parent();
            }

            depth--;
        }

        return tmp;
    }

    /**
     * Json path select
     *
     * @param jpath       json path express
     * @param useStandard use standard mode(default: false)
     * @param cacheJpath  cache json path parsing results
     */
    public ONode select(String jpath, boolean useStandard, boolean cacheJpath) {
        return JsonPath.eval(this, jpath, useStandard, cacheJpath, JsonPath.CRUD.GET);
    }

    /**
     * Json path select
     *
     * @param jpath       json path express
     * @param useStandard use standard mode(default: false)
     */
    public ONode select(String jpath, boolean useStandard) {
        return select(jpath, useStandard, true);
    }

    /**
     * Json path select
     *
     * @param jpath json path express
     */
    public ONode select(String jpath) {
        return select(jpath, false);
    }

    /**
     * Json path select or new（Conditional lookup and functions are not supported）
     *
     * @param jpath json path express
     */
    public ONode selectOrNew(String jpath) {
        return JsonPath.eval(this, jpath, false, true, JsonPath.CRUD.GET_OR_NEW);
    }

    /**
     * Json path delete
     *
     * @param jpath json path express
     * */
//    public void delete(String jpath){
//        JsonPath.eval(this, jpath, false, true, JsonPath.CRUD.REMOVE);
//    }

    /**
     * Json path exists
     *
     * @param jpath json path express
     */
    public boolean exists(String jpath) {
        return select(jpath).isUndefined() == false;
    }

    /**
     * 使用路径（把当前作为根级，深度生成每个子节点的路径）
     * */
    public ONode usePaths(){
        //一般只在根级生成一次
        JsonPath.resolvePath("$", this);
        return this;
    }

    /**
     * 获取路径属性（可能为 null；比如临时集合）
     * */
    public String path(){
        //usePaths 后有效
        return this.attrGet("$PATH");
    }

    /**
     * 获取节点路径列表（如果是临时集合，会提取多个路径）
     * */
    public List<String> pathList() {
        //usePaths 后有效
        List<String> rst = new ArrayList<>();
        JsonPath.extractPath(rst, this);
        return rst;
    }

    /**
     * 将节点切换为对象
     *
     * @return self:ONode
     */
    public ONode asObject() {
        _d.tryInitObject();
        return this;
    }

    /**
     * 将节点切换为数组
     *
     * @return self:ONode
     */
    public ONode asArray() {
        _d.tryInitArray();
        return this;
    }

    /**
     * 将节点切换为值
     *
     * @return self:ONode
     */
    public ONode asValue() {
        _d.tryInitValue();
        return this;
    }

    /**
     * 将节点切换为null
     *
     * @return self:ONode
     */
    public ONode asNull() {
        _d.tryInitNull();
        return this;
    }

    /**
     * 节点数据
     *
     * @return ONodeData
     * @see ONodeData
     */
    public ONodeData nodeData() {
        return _d;
    }

    /**
     * 节点类型
     *
     * @return ONodeType
     * @see ONodeType
     */
    public ONodeType nodeType() {
        return _d.nodeType;
    }

    /**
     * 切换选项
     *
     * @param opts 选项
     * @return self:ONode
     */
    public ONode options(Options opts) {
        if (opts != null) {
            _o = opts;
        }
        return this;
    }

    /**
     * 定制选项
     */
    public ONode options(Consumer<Options> custom) {
        custom.accept(_o);
        return this;
    }

    /**
     * 获取选项
     */
    public Options options() {
        return _o;
    }


    /**
     * 构建表达式
     *
     * @param custom lambda表达式
     * @return self:ONode
     */
    public ONode build(Consumer<ONode> custom) {
        custom.accept(this);
        return this;
    }

    ////////////////////
    //
    // 值处理
    //
    ////////////////////

    /**
     * 获取节点值数据结构体（如果不是值类型，会自动转换）
     *
     * @return OValue
     * @see OValue
     */
    public OValue val() {
        return asValue()._d.value;
    }

    /**
     * 设置节点值
     *
     * @param val 为常规类型或ONode
     * @return self:ONode
     */
    public ONode val(Object val) {
        if (val == null) {
            _d.tryInitNull();
        } else if (val instanceof ONode) { //支持数据直接copy
            _d = ((ONode) val)._d;
        } else if (val instanceof Map || val instanceof Collection || val.getClass().isArray()) {
            _d = buildVal(val)._d;
        } else {
            _d.tryInitValue();
            _d.value.set(val);
        }

        return this;
    }


    /**
     * 获取节点值并以 String 输出
     * 如果节点为对象或数组类型，则输出json
     */
    public String getString() {
        if (isValue()) {
            return _d.value.getString();
        } else {
            if (isArray()) {
                return toJson();
            }

            if (isObject()) {
                return toJson();
            }

            if (_o.hasFeature(Feature.StringNullAsEmpty)) {
                return "";
            } else {
                return null;
            }
        }
    }


    /**
     * 获取节点值并以 short 输出
     */
    public short getShort() {
        if (isValue())
            return _d.value.getShort();
        else
            return 0;
    }

    /**
     * 获取节点值并以 int 输出
     */
    public int getInt() {
        if (isValue())
            return _d.value.getInt();
        else
            return 0;
    }

    /**
     * 获取节点值并以 long 输出
     */
    public long getLong() {
        if (isValue())
            return _d.value.getLong();
        else
            return 0;
    }

    /**
     * 获取节点值并以 float 输出
     */
    public float getFloat() {
        if (isValue())
            return _d.value.getFloat();
        else
            return 0;
    }

    /**
     * 获取节点值并以 double 输出
     */
    public double getDouble() {
        if (isValue())
            return _d.value.getDouble();
        else
            return 0;
    }

    /**
     * 获取节点值并以 double 输出
     *
     * @param scale 精度，即小数点长度
     */
    public double getDouble(int scale) {
        double temp = getDouble();

        if (temp == 0)
            return 0;
        else
            return new BigDecimal(temp)
                    .setScale(scale, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
    }


    /**
     * 获取节点值并以 boolean 输出
     */
    public boolean getBoolean() {
        if (isValue())
            return _d.value.getBoolean();
        else
            return false;
    }

    /**
     * 获取节点值并以 Date 输出
     */
    public Date getDate() {
        if (isValue())
            return _d.value.getDate();
        else
            return null;
    }

    /**
     * 获取节点值并以 char 输出
     */
    public char getChar() {
        if (isValue())
            return _d.value.getChar();
        else
            return 0;
    }

    /**
     * 获取节点的值并以 rawString 输出
     * 如果节点不是值类型，则输出null
     */
    public String getRawString() {
        if (isValue()) {
            return _d.value.getRawString();
        } else {
            return null;
        }
    }

    /**
     * 获取节点的值并以 rawNumber 输出
     * 如果节点不是值类型，则输出null
     */
    public Number getRawNumber() {
        if (isValue())
            return _d.value.getRawNumber();
        else
            return null;
    }

    /**
     * 获取节点的值并以 rawBoolean 输出
     * 如果节点不是值类型，则输出null
     */
    public Boolean getRawBoolean() {
        if (isValue())
            return _d.value.getRawBoolean();
        else
            return null;
    }

    /**
     * 获取节点的值并以 rawDate 输出
     * 如果节点不是值类型，则输出null
     */
    public Date getRawDate() {
        if (isValue())
            return _d.value.getRawDate();
        else
            return null;
    }

    ////////////////////
    //
    // 对象与数组公共处理
    //
    ////////////////////

    /**
     * 清空子节点（对象或数组有效）
     */
    public void clear() {
        if (isObject()) {
            _d.object.clear();
        } else if (isArray()) {
            _d.array.clear();
        }
    }

    /**
     * 子节点数量（对象或数组有效）
     */
    public int count() {
        if (isObject()) {
            return _d.object.size();
        }

        if (isArray()) {
            return _d.array.size();
        }

        return 0;
    }

    ////////////////////
    //
    // 对象处理
    //
    ////////////////////

    /**
     * 获取节点对象数据结构体（如果不是对象类型，会自动转换）
     *
     * @return Map<String, ONode>
     */
    public Map<String, ONode> obj() {
        return asObject()._d.object;
    }

    /**
     * 是否存在对象子节点
     */
    public boolean contains(String key) {
        if (isObject()) {
            return _d.object.containsKey(key);
        } else {
            return false;
        }
    }

    /**
     * 重命名一个子节点（如果不存在则跳过）
     */
    public ONode rename(String key, String newKey) {
        if (key == null || newKey == null) {
            return this;
        }

        if (key.equals(newKey) == false) {
            if (isObject()) {
                rename_do(this, key, newKey);
            } else if (isArray()) {
                for (ONode n : _d.array) {
                    rename_do(n, key, newKey);
                }
            }
        }

        return this;
    }

    private static void rename_do(ONode n, String key, String newKey) {
        if (n.isObject()) {
            ONode tmp = n._d.object.get(key);
            if (tmp != null) {
                n._d.object.put(newKey, tmp);
                n._d.object.remove(key);
            }
        }
    }

    /**
     * 获取对象子节点（不存在，生成新的子节点并返回）
     *
     * @return child:ONode
     */
    public ONode get(String key) {
        _d.tryInitObject();

        ONode tmp = _d.object.get(key);
        if (tmp == null) {
            return new ONode(this, _o);
        } else {
            return tmp;
        }
    }

    public ONode getOrNew(String key) {
        return getOrNew(key, ONodeType.Null);
    }

    public ONode getOrNew(String key, ONodeType newNodeType) {
        _d.tryInitObject();

        ONode tmp = _d.object.get(key);
        if (tmp == null) {
            tmp = new ONode(this, _o);
            if (newNodeType == ONodeType.Object) {
                tmp.asObject();
            } else if (newNodeType == ONodeType.Array) {
                tmp.asArray();
            }

            _d.object.put(key, tmp);
        }

        return tmp;
    }

    /**
     * 获取对象子节点（不存在，返回null）
     *
     * @return child:ONode
     */
    public ONode getOrNull(String key) {
        if (isObject()) {
            return _d.object.get(key);
        } else {
            return null;
        }
    }

    /**
     * 生成新的对象子节点，会清除之前的数据
     *
     * @return child:ONode
     */
    public ONode getNew(String key) {
        _d.tryInitObject();

        ONode tmp = new ONode(this, _o);
        _d.object.put(key, tmp);

        return tmp;
    }

    private ONode buildVal(Object val) {
        if (val instanceof Map) {
            return new ONode(this, _o).setAll((Map<String, ?>) val);
        } else if (val instanceof Collection) {
            return new ONode(this, _o).addAll((Collection<?>) val);
        } else {
            //可能会影响性能...
            //
            if (val != null && val.getClass().isArray()) {
                return new ONode(this, _o).addAll(Arrays.asList((Object[]) val));
            }
            return new ONode(this, _o).val(val);
        }
    }

    /**
     * 设置对象的子节点（会自动处理类型）
     *
     * @param val 为常规类型或ONode
     * @return self:ONode
     */
    public ONode set(String key, Object val) {
        _d.tryInitObject();

        if (val instanceof ONode) {
            setNode(key, ((ONode) val));
        } else {
            setNode(key, buildVal(val));
        }

        return this;
    }

    /**
     * 设置对象的子节点，值为ONode类型 (需要在外部初始化类型)
     *
     * @return self:ONode
     */
    public ONode setNode(String key, ONode val) {
        _d.object.put(key, val);

        if (val._p == null) {
            val._p = this;
        }

        return this;
    }

    /**
     * 设置对象的子节点，将obj的子节点搬过来
     *
     * @param obj 对象类型的节点
     * @return self:ONode
     */
    public ONode setAll(ONode obj) {
        _d.tryInitObject();

        if (obj != null && obj.isObject()) {
            for (Map.Entry<String, ONode> kv : obj._d.object.entrySet()) {
                setNode(kv.getKey(), kv.getValue());
            }
        }

        return this;
    }

    /**
     * 设置对象的子节点，将map的成员搬过来
     *
     * @return self:ONode
     */
    public <T> ONode setAll(Map<String, T> map) {
        _d.tryInitObject();

        if (map != null) {
            map.forEach(this::set);
        }
        return this;
    }

    /**
     * 设置对象的子节点，将map的成员搬过来，并交由代理处置
     *
     * @return self:ONode
     */
    public <T> ONode setAll(Map<String, T> map, BiConsumer<ONode, T> handler) {
        _d.tryInitObject();

        if (map != null) {
            map.forEach((k, v) -> {
                handler.accept(this.get(k), v);
            });
        }
        return this;
    }

    /**
     * 移除对象的子节点 (搞不清楚是自身还是被移除的，所以不返回)
     */
    public void remove(String key) {
        if (isObject()) {
            _d.object.remove(key);
        }
    }

    ////////////////////
    //
    // 数组处理
    //
    ////////////////////

    /**
     * 获取节点数组数据结构体（如果不是数组，会自动转换）
     *
     * @return List<ONode>
     */
    public List<ONode> ary() {
        return asArray()._d.array;
    }

    /**
     * 获取数组子节点（超界，返回空节点） //支持倒数取
     *
     * @return child:ONode
     */
    public ONode get(int index) {
        _d.tryInitArray();

        if (index >= 0 && _d.array.size() > index) {
            return _d.array.get(index);
        }

        return new ONode(this, _o);
    }

    public ONode getOrNew(int index) {
        return getOrNew(index, ONodeType.Null);
    }

    public ONode getOrNew(int index, ONodeType newNodeType) {
        _d.tryInitArray();

        if (_d.array.size() > index) {
            return _d.array.get(index);
        } else {
            ONode tmp = null;
            for (int i = _d.array.size(); i <= index; i++) {
                tmp = new ONode(this, _o);

                if (newNodeType == ONodeType.Object) {
                    tmp.asObject();
                } else if (newNodeType == ONodeType.Array) {
                    tmp.asArray();
                }

                _d.array.add(tmp);
            }
            return tmp;
        }
    }

    /**
     * 获取数组子节点（超界，返回null）
     *
     * @return child:ONode
     */
    public ONode getOrNull(int index) {
        if (isArray()) {
            if (index >= 0 && _d.array.size() > index) {
                return _d.array.get(index);
            }
        }

        return null;
    }

    /**
     * 移除数组的子节点(搞不清楚是自身还是被移除的，所以不返回)
     */
    public void removeAt(int index) {
        if (isArray()) {
            _d.array.remove(index);
        }
    }

    /**
     * 生成新的数组子节点
     *
     * @return child:ONode
     */
    public ONode addNew() {
        _d.tryInitArray();

        ONode n = new ONode(this, _o);
        _d.array.add(n);
        return n;
    }

    /**
     * 添加数组子节点
     *
     * @param val 为常规类型或ONode
     * @return self:ONode
     */
    public ONode add(Object val) {
        _d.tryInitArray();

        if (val instanceof ONode) {
            addNode((ONode) val);
        } else {
            addNode(buildVal(val));
        }

        return this;
    }

    /**
     * 添加数组子节点，值为ONode类型 (需要在外部初始化类型)
     *
     * @return self:ONode
     */
    public ONode addNode(ONode val) {
        _d.array.add(val);
        if (val._p == null) {
            val._p = this;
        }

        return this;
    }

    /**
     * 添加数组子节点，将ary的子节点搬过来
     *
     * @param ary 数组类型的节点
     * @return self:ONode
     */
    public ONode addAll(ONode ary) {
        _d.tryInitArray();

        if (ary != null && ary.isArray()) {
            for (ONode n1 : ary._d.array) {
                //_p
                addNode(n1);
            }
        }

        return this;
    }

    /**
     * 添加数组子节点，将ary的成员点搬过来
     *
     * @return self:ONode
     */
    public <T> ONode addAll(Collection<T> ary) {
        _d.tryInitArray();

        if (ary != null) {
            ary.forEach(m -> add(m));
        }
        return this;
    }

    /**
     * 添加数组子节点，将ary的成员点搬过来，并交由代理处置
     *
     * @return self:ONode
     */
    public <T> ONode addAll(Collection<T> ary, BiConsumer<ONode, T> handler) {
        _d.tryInitArray();

        if (ary != null) {
            ary.forEach(m -> handler.accept(addNew(), m));
        }
        return this;
    }

    //////////////////////

    /**
     * 检查节点是否未定义
     */
    public boolean isUndefined() {
        return _d.nodeType == ONodeType.Null;
    }

    /**
     * 检查节点是否为null
     */
    public boolean isNull() {
        return (_d.nodeType == ONodeType.Null) || (isValue() && _d.value.isNull());
    }

    /**
     * 检查节点是否为值
     */
    public boolean isValue() {
        return _d.nodeType == ONodeType.Value;
    }

    /**
     * 检查节点是否为对象
     */
    public boolean isObject() {
        return _d.nodeType == ONodeType.Object;
    }

    /**
     * 检查节点是否为数组
     */
    public boolean isArray() {
        return _d.nodeType == ONodeType.Array;
    }

    //////////////////////


    /**
     * 遍历对象的子节点
     */
    public ONode forEach(BiConsumer<String, ONode> consumer) {
        if (isObject()) {
            _d.object.forEach(consumer);
        }

        return this;
    }

    /**
     * 遍历数组的子节点
     */
    public ONode forEach(Consumer<ONode> consumer) {
        if (isArray()) {
            _d.array.forEach(consumer);
        }

        return this;
    }

    ////////////////////
    //
    // 特性处理
    //
    ////////////////////

    /**
     * 获取特性
     */
    public String attrGet(String key) {
        return _d.attrGet(key);
    }

    /**
     * 设置特性
     */
    public ONode attrSet(String key, String val) {
        _d.attrSet(key, val);
        return this;
    }

    /**
     * 遍历特性
     */
    public ONode attrForeach(BiConsumer<String, String> consumer) {
        if (_d.attrs != null) {
            _d.attrs.forEach(consumer);
        }
        return this;
    }

    ////////////////////
    //
    // 转换操作
    //
    ////////////////////

    /**
     * 将当前ONode 转为 string（由 stringToer 决定）
     */
    @Override
    public String toString() {
        return to(DEFAULTS.DEF_STRING_TOER);
    }

    /**
     * 将当前ONode 转为 json string
     */
    public String toJson() {
        return to(DEFAULTS.DEF_JSON_TOER);
    }

    /**
     * 将当前ONode 转为 数据结构体（Map or List or val）
     */
    public Object toData() {
        return to(DEFAULTS.DEF_OBJECT_TOER);
    }

    /**
     * 将当前ONode 转为 java object
     * <p>
     * clz = Object.class   => auto typ
     */
    public <T> T toObject() {
        return toObject(Object.class);
    }

    /**
     * 将当前ONode 转为 java object
     * <p>
     * clz = XxxModel.class => XxxModel
     * clz = Object.class   => auto type
     * clz = null           => Map or List or Value
     */
    public <T> T toObject(Type clz) {
        return to(DEFAULTS.DEF_OBJECT_TOER, clz);
    }


    /**
     * 将当前ONode 转为 java list
     * <p>
     * clz = XxxModel.class => XxxModel
     * clz = Object.class   => auto type
     * clz = null           => Map or List or Value
     */
    public <T> List<T> toObjectList(Class<T> clz) {
        List<T> list = new ArrayList<>();

        for (ONode n : ary()) {
            list.add(n.toObject(clz));
        }

        return list;
    }

    @Deprecated
    public <T> List<T> toArray(Class<T> clz) {
        return toObjectList(clz);
    }

    /**
     * 将当前ONode 通过 toer 进行转换
     */
    public <T> T to(Toer toer, Type clz) {
        return (T) (new Context(_o, this, clz).handle(toer).target);
    }

    public <T> T to(Toer toer) {
        return to(toer, null);
    }

    /**
     * 绑定到
     */
    public <T> T bindTo(T target) {
        Context ctx = new Context(_o, this, target.getClass());
        ctx.target = target;
        ctx.handle(DEFAULTS.DEF_OBJECT_TOER);

        return target;
    }


    /**
     * 填充数据（如有问题会跳过，不会出异常）
     *
     * @param source 可以是 String 或 java object 数据
     * @return self:ONode
     */
    public ONode fill(Object source) {
        val(doLoad(source, source instanceof String, _o, null));
        return this;
    }

    public ONode fill(Object source, Feature... features) {
        val(doLoad(source, source instanceof String, Options.def().add(features), null));
        return this;
    }

    public ONode fillObj(Object source, Feature... features) {
        val(doLoad(source, false, Options.def().add(features), null));
        return this;
    }

    public ONode fillStr(String source, Feature... features) {
        val(doLoad(source, true, Options.def().add(features), null));
        return this;
    }

    ////////////////////
    //
    // 来源加载
    //
    ////////////////////

    /**
     * 加载数据并生成新节点（如果异常，会生成空ONode）
     *
     * @param source 可以是 String 或 java object 数据
     * @return new:ONode
     */
    public static ONode load(Object source) {
        return load(source, null, null);
    }

    /**
     * @param features 特性
     */
    public static ONode load(Object source, Feature... features) {
        return load(source, Options.def().add(features), null);
    }

    /**
     * @param opts 常数配置
     */
    public static ONode load(Object source, Options opts) {
        return load(source, opts, null);
    }


    /**
     * @param fromer 来源处理器
     */
    public static ONode load(Object source, Options opts, Fromer fromer) {
        return doLoad(source, source instanceof String, opts, fromer);
    }

    /**
     * 加载string并生成新节点
     */
    public static ONode loadStr(String source) {
        return doLoad(source, true, null, null);
    }

    public static ONode loadStr(String source, Options opts) {
        return doLoad(source, true, opts, null);
    }

    public static ONode loadStr(String source, Feature... features) {
        return doLoad(source, true, Options.def().add(features), null);
    }

    /**
     * 加载java object并生成新节点
     */
    public static ONode loadObj(Object source) {
        return doLoad(source, false, null, null);
    }

    //loadStr 不需要 opts
    public static ONode loadObj(Object source, Options opts) {
        return doLoad(source, false, opts, null);
    }

    public static ONode loadObj(Object source, Feature... features) {
        return doLoad(source, false, Options.def().add(features), null);
    }


    private static ONode doLoad(Object source, boolean isString, Options opts, Fromer fromer) {
        if (fromer == null) {
            if (isString) {
                fromer = DEFAULTS.DEF_STRING_FROMER;
            } else {
                fromer = DEFAULTS.DEF_OBJECT_FROMER;
            }
        }

        if (opts == null) {
            opts = Options.def();
        }

        return (ONode) new Context(opts, source).handle(fromer).target;
    }

    ////////////////////
    //
    // 字符串化
    //
    ////////////////////

    /**
     * 字会串化 （由序列化器决定格式）
     *
     * @param source java object
     * @throws Exception
     */
    public static String stringify(Object source) {
        return stringify(source, Options.def());
    }

    /**
     * @deprecated 3.2.40
     */
    @Deprecated
    public static String stringify(Object source, Feature... features) {
        if (features.length > 0) {
            return stringify(source, new Options(Feature.of(features)));
        } else {
            return stringify(source, Options.def());
        }
    }

    /**
     * 字会串化 （由序列化器决定格式）
     *
     * @param source java object
     * @param opts   常量配置
     * @throws Exception
     */
    public static String stringify(Object source, Options opts) {
        //加载java object，须指定Fromer
        return load(source, opts, DEFAULTS.DEF_OBJECT_FROMER).toString();
    }

    ////////////////////
    //
    // 序列化
    //
    ////////////////////

    /**
     * 序列化为 string（由序列化器决定格式）
     *
     * @param source java object
     * @throws Exception
     */
    public static String serialize(Object source) {
        //加载java object，须指定Fromer
        return load(source, Options.serialize(), DEFAULTS.DEF_OBJECT_FROMER).toJson();
    }

    /**
     * 反序列化为 java object（由返序列化器决定格式）
     *
     * @param source string
     * @throws Exception
     */
    public static <T> T deserialize(String source) {
        return deserialize(source, Object.class);
    }

    /**
     * 反序列化为 java object（由返序列化器决定格式）
     *
     * @param source string
     * @throws Exception
     */
    public static <T> T deserialize(String source, Type clz) {
        //加载String，不需指定Fromer
        return load(source, Options.serialize(), null).toObject(clz);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return isNull();
        }

        if (isArray()) {
            if (o instanceof ONode) {
                return Objects.equals(ary(), ((ONode) o).ary());
            } else {
                return Objects.equals(ary(), o);
            }
        }

        if (isObject()) {
            if (o instanceof ONode) {
                return Objects.equals(obj(), ((ONode) o).obj());
            } else {
                return Objects.equals(obj(), o);
            }
        }

        if (isValue()) {
            if (o instanceof ONode) {
                return Objects.equals(val(), ((ONode) o).val());
            } else {
                return Objects.equals(val(), o);
            }
        }

        //最后是null type
        if (o instanceof ONode) {
            return ((ONode) o).isNull(); //都是 null
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return _d.hashCode();
    }
}