# ApplicationContextAware详解
在我们需要使用ApplicationContext的服务中实现ApplicationContextAware接口,系统启动时就可以自动给我们的服务注入applicationContext对象,我们就可以获取到ApplicationContext里的所有信息了。

## 使用示例
```java
@Service("gatewayService")
public class GatewayServiceImpl implements IGatewayService,ApplicationContextAware {

    private Logger logger= LoggerFactory.getLogger(getClass());


    Map<ServiceBeanEnum,IGatewayBo> chargeHandlerMap=new HashMap<ServiceBeanEnum,IGatewayBo>();

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext=applicationContext;
    }
}
```

## 原理分析
我们都知道spring的入口方法就在AbstractApplicationContext的refresh()方法.我们先去看看refresh().


### 入口方法 AbstractApplicationContext#refresh
org.springframework.context.support.AbstractApplicationContext#refresh
```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // 容器重启同步监控锁，防止刷新进行到一半被重复执行
    synchronized (this.startupShutdownMonitor) {
        // 填充配置文件占位符，记录容器启动时间和启动状态
        prepareRefresh();

        // 1. 获得新的BeanFactory定义,完成配置文件定义到注册表登记bean的流程，此时对象还未被创建
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // 准备工厂，主要设置classloader、设置自动注入时需要忽略的类、设置工厂、添加BeanPostProcessor可供回调
        prepareBeanFactory(beanFactory);

        try {
            // 注册实现了 BeanPostProcessor 接口的 bean
            postProcessBeanFactory(beanFactory);

            // 2. 初始化和执行 实现了 BeanFactoryPostProcessor beans
            invokeBeanFactoryPostProcessors(beanFactory);

            // 3. 初始化和执行 实现了BeanPostProcessor beans，bean扩展:postProcessBeforeInitialization和postProcessAfterInitialization，分别在Bean初始化之前和初始化之后得到执行
            registerBeanPostProcessors(beanFactory);

            // 初始化MessageSource对象，国际化
            initMessageSource();

            // 初始化事件广播器（可理解为事件发送者）
            initApplicationEventMulticaster();

            // 调用子类refresh扩展，初始化特殊的类，默认该方法什么都不做，临时钩子方法，提供一些初始化完成前的特殊操作
            onRefresh();

            // 注册事件监听器
            registerListeners();

            // Instantiate all remaining (non-lazy-init) singletons.
            // 4. 完成对象实例化入口（创建非延迟加载的单例对象）
            finishBeanFactoryInitialization(beanFactory);

            // 完成刷新，发布容器刷新事件
            finishRefresh();
        } catch (BeansException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Exception encountered during context initialization - " + "cancelling refresh attempt: " + ex);
            }
            // Destroy already created singletons to avoid dangling resources.
            destroyBeans();

            // Reset 'active' flag.
            cancelRefresh(ex);

            // Propagate exception to caller.
            throw ex;
        }

        finally {
            // Reset common introspection caches in Spring's core, since we
            // might not ever need metadata for singleton beans anymore...
            resetCommonCaches();
        }
    }
}
```
### AbstractApplicationContext#prepareBeanFactory 初始化BeanFactory

AbstractApplicationContext#prepareBeanFactory 方法
`org.springframework.context.support.AbstractApplicationContext#prepareBeanFactory` 

```java
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // Tell the internal bean factory to use the context's class loader etc.
    beanFactory.setBeanClassLoader(getClassLoader());
    beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
    beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

    // 添加ApplicationContextAware的处理器
    beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
    beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
    beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
    beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
            ...
}
```

也就是说,spring在启动的时候给我们添加了ApplicationContextAwareProcessor这样一个processor。进去看看它的实现:
```java
@Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        AccessControlContext acc = null;

        if (System.getSecurityManager() != null &&
                (bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
                        bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
                        bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware)) {
            acc = this.applicationContext.getBeanFactory().getAccessControlContext();
        }

        if (acc != null) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                   //核心方法,调用aware接口方法
                    invokeAwareInterfaces(bean);
                    return null;
                }
            }, acc);
        }
        else {
            invokeAwareInterfaces(bean);
        }

        return bean;
    }
    //实现
    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof EnvironmentAware) {
                ((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
            }
            if (bean instanceof EmbeddedValueResolverAware) {
                ((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
            }
            if (bean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
            }
            if (bean instanceof ApplicationEventPublisherAware) {
                ((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
            }
            if (bean instanceof MessageSourceAware) {
                ((MessageSourceAware) bean).setMessageSource(this.applicationContext);
            }
          //针对实现了ApplicationContextAware的接口,spring都将调用其setApplicationContext,将applicationContext注入到当前bean对象。
            if (bean instanceof ApplicationContextAware) {
                ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
            }
        }
    }

```

那ApplicationContextAwareProcessor又是什么时候调用的呢？

### ConfigurableListableBeanFactory#preInstantiateSingletons 方法
org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons

```java
拿到所有的beanNames,然后依次判断是否需要加载,如果是,则调用getBean(beanName)方法实例化出来。
// Trigger initialization of all non-lazy singleton beans...
for (String beanName : beanNames) {
    RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
    if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
        if (isFactoryBean(beanName)) {
            final FactoryBean<?> factory = (FactoryBean<?>) getBean(FACTORY_BEAN_PREFIX + beanName);
            boolean isEagerInit;
            if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                isEagerInit = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                    @Override
                    public Boolean run() {
                        return ((SmartFactoryBean<?>) factory).isEagerInit();
                    }
                }, getAccessControlContext());
            }
            else {
                isEagerInit = (factory instanceof SmartFactoryBean &&
                        ((SmartFactoryBean<?>) factory).isEagerInit());
            }
            if (isEagerInit) {
                getBean(beanName);
            }
        }
        else {
            getBean(beanName);
        }
    }
}
```

### 依次查看getBean() ->doGetBean()->createBean()->doCreateBean()方法

org.springframework.beans.factory.support.AbstractBeanFactory#getBean(java.lang.String)
org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean
org.springframework.beans.factory.support.AbstractBeanFactory#createBean
org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean
```java
// Initialize the bean instance.
Object exposedObject = bean;
try {
    populateBean(beanName, mbd, instanceWrapper);
    if (exposedObject != null) {
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    }
}
catch (Throwable ex) {
    if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
        throw (BeanCreationException) ex;
    }
    else {
        throw new BeanCreationException(
                mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
    }
}
```

### 查看BeanFactory的initializeBean方法
> AbstractAutowireCapableBeanFactory 的 initializeBean 方法封装了Bean初始化的全部过程

org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)

AbstractAutowireCapableBeanFactory#initializeBean方法如下:
```java
protected Object initializeBean(final String beanName, final Object bean, RootBeanDefinition mbd) {
    if (System.getSecurityManager() != null) {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                AbstractAutowireCapableBeanFactory.this.invokeAwareMethods(beanName, bean);
                return null;
            }
        }, this.getAccessControlContext());
    } else {
        this.invokeAwareMethods(beanName, bean);
    }
    // 1. bean 初始化之前, 触发BeanPostProcessor的postProcessBeforeInitialization
    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }
    
    // 2 . 初始化bean
    try {
        this.invokeInitMethods(beanName, wrappedBean, mbd);
    } catch (Throwable var6) {
        Throwable ex = var6;
        throw new BeanCreationException(mbd != null ? mbd.getResourceDescription() : null, beanName, "Invocation of init method failed", ex);
    }
    
    // 3. bean 初始化之后, 触发BeanPostProcessor的postProcessAfterInitialization
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

    return wrappedBean;
}
```

原来AbstractAutowireCapableBeanFactory中的inititalBean()方法就是BeanPostProcessor的调用处。但是像BeanNameAware、BeanFactoryAware不同,是通过initialBean()中的invokeAwareMethods直接调用实现的。

链接：https://www.jianshu.com/p/22e7fba07ce7