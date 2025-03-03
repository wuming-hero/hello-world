# switch 原理

## switch声明
1. 必须为static修饰的静态成功变量
2. 不可使用final修饰
3. 成员变量上增加@AppSwitch 注解

```java
public class SwitchConfig {

    @AppSwitch(des = "String 类型开关", level = Level.p2)
    public static String stringSwitch = "string";
}
```

## 注册及初始化

### 项目启动时初始化注册开关
```java
/** 
    应用调用此方法完成注册, 应用名请保持和Aone一致, 大小写敏感。同时请保证应用在启动的时候, 调用过且至少用过一次此方法,多次调用会抛出异常。
 1.常量类参数是可变参数, 可注册多个常量类。
 2.如常量类未添加 com.taobao.csp.switchcenter.annotation.NameSpace 注解, 默认使用完整类的全限定名作为namespace。
 */
SwitchManager.init("appName", SwitchConfig.class, SwitchConfig2.class);
// 注册监听可以添加多个
SwitchManager.addListener(this);
// 不回的listener需要继承com.taobao.csp.switchcenter.core.Listener
SwitchManager.addListener("com.xxx.SwitchChangeListener");
```

### 注册单个开关

1. 首先会通过反射获取常量类定义的所有字段，

```java
Field[] fields = switchClass.getDeclaredFields();
```
2. 然后会判断该成员变量上是否有@AppSwitch注解，如果没有的话，该变量只是普通的java变量，不会做任何特殊处理。

3. 将成员变量所有值封装到类 Switch 中
就是将注解上获取到的全部信息都存储到switchBean里面。
其中name为属性字段的变量名，
strValue为上文提到的针对byte和Byte变量特殊处理后的字符串（如果不是这两类变量，就是直接fastjson序列化的结果），
defaultValue为直接序列化的字符串。
getValueConfig获取的注解上的开关可选取值。
```java
Switch switchBean = new Switch(name, strValue, nameSpaceString, annotation.valueDes(), annotation.des(), defaultValue,
SwitchUtil.getValueConfig(annotation.values()), annotation.level(), field.getType());
```

4. 注册到开关容器中 
* 首先是注册switchBean到switches map中，Map<String, Map<String, Switch>> switches = new HashMap<String, Map<String, Switch>>();，外层key为appName，内层key为switchBean的key。

* 然后是注册默认序列化后的值defaultValue到switchFieldsDefaultValue map中，Map<String, Map<String, String>> switchFieldsDefaultValue = new HashMap<String, Map<String, String>>();内外两层的key含义同switches;

* 注册当前属性字段的Field对象到容器中。private static Map<String, Map<String, Field>> switchFields = new HashMap<String, Map<String, Field>>();

* 最后，是将属性字段的getter和setter方法注册到容器里。通过反射getDeclaredMethod获取属性字段对应的getter和fatter方法，如果出了异常，即常量类没有定义对应的getter和setter方法则对应方法为null。
注册完成之后，SwitchContainer容器中就包含了开关所需的所有信息了
```java
public static synchronized void register(String appName, String key, Switch switchBean,
                                String defaultValue, Field field, Method getMethod, Method setMethod) {
    registerSwitchBean(appName, key, switchBean);
    registerSwitchDefaultValue(appName, key, defaultValue);
    registerSwitchField(appName, key, field);
    registerSwitchGetMethod(appName, key, getMethod);
    registerSwitchSetMethod(appName, key, setMethod);

}

```
### 初始化变量值（内存值）
1. 首先会根据appName和key从SwtichContainer中取出注册的SwitchBean，
2. 然后取出对应的Field对象， 最后反射调用Field.set(null,newValue)即可更改内存中的开关值。
3. 如果设置成功了，并且配置了对应开关配置了回调类，那么会执行一次回调方法。 
   3.1 如果回调方法执行成功了，那么会修改switchBean的lastModifiedTime，并且通知所有的Listener进行处理。
   3.2 如果callBack执行失败了，switch会回滚diamond里面获取的值，并输出日志然后抛出异常，Switch启动失败。

### 持久化开关
switch开关也支持持久化，如果没有持久化的开关，每次注册之后都会使用代码里面的值。
如果持久化了，那么会在注册开关之后初始化上次持久化的值。
```java
registerSwitch(appName, switchClasses);
DiamondManager.initPersistenceSwitch(appName);
```

从上面的代码中可以看出，switch持久化使用的是diamond，它的入参是appName。我们来详细看看这个逻辑。
从diamond初始化开关值
```java
/**
* @param appName
* Application name, case sensitive.
*/
public static void initPersistenceSwitch(final String appName) {
Map<String, String> persistenceConfig = Diamond.getConfigByAppName(appName, PERSISTENCE_GROUP);
initPersistenceSwitchFromDiamond(appName, persistenceConfig);

    synchronized (appNamesSet) {
        // avoid duplicate diamond listener
        if (!appNamesSet.contains(appName)) {
            addSwitchValueChangerDiamondListener(appName, PERSISTENCE_GROUP);
            appNamesSet.add(appName);
        }
    }
}
```
从上面可以看出，从diamond获取配置的dataId为appName,groupId为字符串常量"asp-switch". 
diamond里面持久化开关的配置格式是Map序列化的字符串。
配置Map的key为上文switchBean的key(即nameSpace.name，命名空间.变量名,
value即为对应开关的序列化字符串。 对应diamond上的每一个配置，会去修改内存中的实际值。


## 推送修改switch值
我们在控制台对开关所做的操作都是调用这些API，有应用程序服务器进行处理的。
Web控制台只是对这些指令做了一层可视化的封装，便于开发人员使用。
如果是非持久化的操作，还可以选择对应用集群的单台集群进行操作，由应用服务器直接处理相应的变更，并且更新内存中的值。
如果是持久化操作，那么在控制台做的操作，都会直接写入diamond的配置项中，应用服务器通过diamond的listener去更新内存中的值。

## 本地容灾文件
Switch也支持本地容灾文件。
在Switch启动的时候会进行本地容灾文件配置的监听listenLocalFile。Switch的容灾文件目录为/home/admin/csp/switch,如果应用启动时不存在，会新建对应的目录文件，对应注册的appName，其容灾文件的完整路径为/home/admin/csp/switch/appName.json。
如果不存在也会新建一个空文件。对其会注册相应的listen，监听fileCreated、fileModified、fileDelted事件。本地容灾文件的内容和diamond上持久化配置信息格式一个，也是一个map序列化的字符串.如果本地文件发生变化，对应的listener会进行处理，找出开关值发生变化的key，然后更新内存取值。
至此Switch初始化也就结束了。

## 总结
* Switch是轻量级的开关中间件，支持动态变更、也支持持久化配置。Switch内部依赖了diamond，用于持久化开关值管理
* Switch本身就是服务端，接受外部通过API请求的数据变更（持久化推送直接通过Diamond），每一次数据变更的推送都是一个Http 请求。
* Switch变量的初始取值是代码中的值。也就是说如果你的开关值在Switch初始化完成之前就被使用了，那么可能持久化的值还没有从diamond上获取，那你使用的就是代码中的赋值语句的取值了。

