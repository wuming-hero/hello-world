# 原理

## 初始化loader
1. 创建实例Initializer
2. 初始化ScanConfigLoader 和 XmlConfigLoader
3. 调用Initializer.init()

## 根据包路径扫描加载组件
ScanConfigLoader： 根据包路径扫描
XmlConfigLoader： 根据xml文件中的配置扫描

### 初始化垂直插件 和 水平模板
> 此处仅限当前类，并不初始垂直插件关联的扩展点和水平插件

1.configLoader.load(configRegister); 会扫描传入的包路径，
把该包下的所有继承Plugin，Template，Facade，Extension 的类都收集起来；暂存在Configuration对象的pluginMap 和 templateMap中

## 初始化水平插件 和垂直插件中的扩展点和模板
registerConfiguration
### 初始化水平插件关联的扩展点或水平模板
PluginManager.getInstance().register(plugins);

### 初始化垂直插件关联的扩展点或水平模板
TemplateManager.getInstance().registerTemplate(template)

PluginManager 和 TemplateManager 都是单例，里面维护了pluginMap 和 templateMap 用来存储扩展点信息

### 全局模板和扩展点关联 
associateGlobalTemplates

## 业务身份判断
bizSession.identifyByType 过程会去查找具体的业务对应垂直插件

## 查询具体扩展点执行
ExtensionExecutor.findExtensionRealizations 在plugin中去遍历查找扩展点

# 场景执行
## ActionChainManager#init 初始化
ActionChainManager#init
### 获得所有的action，记录在 ActionPool中的map中
Map<String, IAction> beans = applicationContext.getBeansOfType(IAction.class);

### 加载场景配置xml文件
将场景--action配置文件转换为java对象

### 加载实现ISceneRegistrar声明的场景









