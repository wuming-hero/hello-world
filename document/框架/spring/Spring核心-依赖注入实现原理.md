## 依赖注入
> 我们常提起的依赖注入(Dependency Injection)和控制反转(Inversion of Control)是同一个概念。 
> 具体含义是:当某个角色(可能是一个Java实例，调用者)需要另一个角色(另一个Java实例，被调用者)的协助时， 在传统的程序设计过程中，通常由调用者来创建被调用者的实例。
> 但在Spring里，创建被调用者的工作不再由调用者来完成，因此称为控制反转; 创建被调用者实例的工作通常由Spring容器来完成，然后注入调用者，因此也称为依赖注入。

其实简单的说，依赖注入起到的作用就是讲对象之间的依赖关系从原先的代码中解耦出来，通过配置文件或注解等方式加上Spring框架的处理让我们对依赖关系灵活集中的进行管理。


## Spring初始化入口 AbstractApplicationContext#refresh 

org.springframework.context.support.AbstractApplicationContext#refresh

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // 容器重启同步监控锁，防止刷新进行到一半被重复执行
    synchronized (this.startupShutdownMonitor) {
        // 填充配置文件占位符，记录容器启动时间和启动状态
        prepareRefresh();

        // Tell the subclass to refresh the internal bean factory.
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // Prepare the bean factory for use in this context.
        prepareBeanFactory(beanFactory);

        try {
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);

            // Invoke factory processors registered as beans in the context.
            invokeBeanFactoryPostProcessors(beanFactory);

            // Register bean processors that intercept bean creation.
            registerBeanPostProcessors(beanFactory);

            // Initialize message source for this context.
            initMessageSource();

            // Initialize event multicaster for this context.
            initApplicationEventMulticaster();

            // Initialize other special beans in specific context subclasses.
            onRefresh();

            // Check for listener beans and register them.
            registerListeners();

            // Instantiate all remaining (non-lazy-init) singletons.
            finishBeanFactoryInitialization(beanFactory);

            // Last step: publish corresponding event.
            finishRefresh();
        }

        catch (BeansException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Exception encountered during context initialization - " +
                        "cancelling refresh attempt: " + ex);
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

## Bean初始化时机
> AbstractAutowireCapableBeanFactory.populateBean()
> Spring可以根据用户标注的@Autowired、@Value、@Inject、@Resource等多种注解来实现自动装配，在框架内部，这个功能的实现依赖于`BeanPostProcessor`机制，更确切地说是`AutowiredAnnotationBeanPostProcessor`为应用程序赋予了autowire能力。
> `AutowiredAnnotationBeanPostProcessor`是在`AbstractAutowireCapableBeanFactory.populateBean()`函数调用发生时介入bean实例化过程中的

1. 在Spring容器启动时，预初始化那些符合条件的Bean（单例、非抽象、非延迟加载），
2. 但对于FactoryBean，需要额外检查是否是SmartFactoryBean，并且是否需要急切初始化，只有在这些情况下才会初始化。
3. 如果bean的scope是prototype的，第一次使用时再进行实例化.

在AbstractApplicationContext的refresh的后面环节会根据beanName遍历去获取对应的实例化bean，这个环节会触发getBean进行初始化。
org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons具体代码如下
```java
@Override
public void preInstantiateSingletons() throws BeansException {
    if (this.logger.isDebugEnabled()) {
        this.logger.debug("Pre-instantiating singletons in " + this);
    }

    // Iterate over a copy to allow for init methods which in turn register new bean definitions.
    // While this may not be part of the regular factory bootstrap, it does otherwise work fine.
    List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

    // Trigger initialization of all non-lazy singleton beans...
    for (String beanName : beanNames) {
        RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
        if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
            if (isFactoryBean(beanName)) {
                Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
                if (bean instanceof FactoryBean) {
                    final FactoryBean<?> factory = (FactoryBean<?>) bean;
                    boolean isEagerInit;
                    if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                        isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)
                                        ((SmartFactoryBean<?>) factory)::isEagerInit,
                                getAccessControlContext());
                    }
                    else {
                        isEagerInit = (factory instanceof SmartFactoryBean &&
                                ((SmartFactoryBean<?>) factory).isEagerInit());
                    }
                    if (isEagerInit) {
                        getBean(beanName);
                    }
                }
            }
            else {
                getBean(beanName);
            }
        }
    }

    // Trigger post-initialization callback for all applicable beans...
    for (String beanName : beanNames) {
        Object singletonInstance = getSingleton(beanName);
        if (singletonInstance instanceof SmartInitializingSingleton) {
            final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                    smartSingleton.afterSingletonsInstantiated();
                    return null;
                }, getAccessControlContext());
            }
            else {
                smartSingleton.afterSingletonsInstantiated();
            }
        }
    }
}

```


## Bean初始化流程

### 1. 调用AbstractBeanFactory#getBean(java.lang.String)
### 2. AbstractBeanFactory#doGetBean
### 3. AbstractAutowireCapableBeanFactory#createBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
### 4. AbstractAutowireCapableBeanFactory#doCreateBean

### 5. AbstractAutowireCapableBeanFactory#createBeanInstance
> 生成了Bean所包含的Java对象

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    // 确认需要创建Bean实例的类可以实例化
    Class<?> beanClass = resolveBeanClass(mbd, beanName);

    if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
        throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
    }

    Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
    if (instanceSupplier != null) {
        return obtainFromSupplier(instanceSupplier, beanName);
    }

    // 使用工厂方法进行实例化
    if (mbd.getFactoryMethodName() != null)  {
        return instantiateUsingFactoryMethod(beanName, mbd, args);
    }

    // Shortcut when re-creating the same bean...
    boolean resolved = false;
    boolean autowireNecessary = false;
    if (args == null) {
        synchronized (mbd.constructorArgumentLock) {
            if (mbd.resolvedConstructorOrFactoryMethod != null) {
                resolved = true;
                autowireNecessary = mbd.constructorArgumentsResolved;
            }
        }
    }
    if (resolved) {
        if (autowireNecessary) {
            return autowireConstructor(beanName, mbd, null, null);
        }
        else {
            return instantiateBean(beanName, mbd);
        }
    }

    // 使用构造函数进行实例化
    Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
    if (ctors != null ||
            mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR ||
            mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args))  {
        return autowireConstructor(beanName, mbd, ctors, args);
    }

    // 默认使用无参构造函数进行实例化
    return instantiateBean(beanName, mbd);
}
```

#### 5.1 AbstractAutowireCapableBeanFactory#instantiateBean 实例化
> 我们重点关注 getInstantiationStrategy() 这个方法，可以看到instantiateBean方法的功能实现是通过调用getInstantiationStrategy().instantiate方法实现的。
getInstantiationStrategy 方法的作用是获得实例化的策略对象，也就是指通过哪种方案进行实例化的过程。
继续跟踪下去我们可以发现，Spring当中提供了两种实例化方案： BeanUtils 和 Cglib 方式。
BeanUtils实现机制是通过Java的反射机制， Cglib是一个第三方类库采用的是一种字节码加强方式机制。 Spring中采用的默认实例化策略是Cglib。`
分析到这里我们已经知道了实例化Bean对象的流程，现在已经是万事具备，只欠东风，就剩下对这些建立好的Bean对象建立联系了。

```java
protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
    try {
        Object beanInstance;
        final BeanFactory parent = this;
        if (System.getSecurityManager() != null) {
            beanInstance = AccessController.doPrivileged((PrivilegedAction<Object>) () ->
                    getInstantiationStrategy().instantiate(mbd, beanName, parent),
                    getAccessControlContext());
        }
        else {
            beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
        }
        BeanWrapper bw = new BeanWrapperImpl(beanInstance);
        initBeanWrapper(bw);
        return bw;
    }
    catch (Throwable ex) {
        throw new BeanCreationException(
                mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
    }
}
```
### 6. AbstractAutowireCapableBeanFactory#populateBean
```java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
    // 获得BeanDefinition中设置的property信息，简单理解依赖注入的过程就是对这些property赋值的过程，而只是property信息在BeanDefinition的解析过程中
    if (bw == null) {
        if (mbd.hasPropertyValues()) {
            throw new BeanCreationException(
                    mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
        }
        else {
            // Skip property population phase for null instance.
            return;
        }
    }

    // Give any InstantiationAwareBeanPostProcessors the opportunity to modify the
    // state of the bean before properties are set. This can be used, for example,
    // to support styles of field injection.
    boolean continueWithPropertyPopulation = true;

    if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
        for (BeanPostProcessor bp : getBeanPostProcessors()) {
            if (bp instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
                if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                    continueWithPropertyPopulation = false;
                    break;
                }
            }
        }
    }

    if (!continueWithPropertyPopulation) {
        return;
    }
    
    PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);

    //开始依赖注入的过程，先处理Autowire的注入，根据自动注入模式调用相应的处理方法
    if (mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_NAME ||
            mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_TYPE) {
        MutablePropertyValues newPvs = new MutablePropertyValues(pvs);

        // Add property values based on autowire by name if applicable.
        if (mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_NAME) {
            autowireByName(beanName, mbd, bw, newPvs);
        }

        // Add property values based on autowire by type if applicable.
        if (mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_TYPE) {
            autowireByType(beanName, mbd, bw, newPvs);
        }

        pvs = newPvs;
    }

    boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
    boolean needsDepCheck = (mbd.getDependencyCheck() != RootBeanDefinition.DEPENDENCY_CHECK_NONE);

    if (hasInstAwareBpps || needsDepCheck) {
        if (pvs == null) {
            pvs = mbd.getPropertyValues();
        }
        PropertyDescriptor[] filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
        if (hasInstAwareBpps) {
            for (BeanPostProcessor bp : getBeanPostProcessors()) {
                if (bp instanceof InstantiationAwareBeanPostProcessor) {
                    InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
                    pvs = ibp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
                    if (pvs == null) {
                        return;
                    }
                }
            }
        }
        if (needsDepCheck) {
            checkDependencies(beanName, mbd, filteredPds, pvs);
        }
    }
    
    //对属性进行注入
    if (pvs != null) {
        applyPropertyValues(beanName, mbd, bw, pvs);
    }
}
```
 

#### 6.1 AbstractAutowireCapableBeanFactory#applyPropertyValues 对属性进行注入
在配置Bean的属性的时候，属性可能有多种类型，我们再进行注入的时候，不同的属性类型我们不可能一概而论的进行处理，集合类型的属性和非集合类型具备很大的差别，对不同的类型应该有不同的解析处理过程，
故该方法流程中首先判断value的类型然后在分别调用 resolveManagedList()、 resolveManagedList() 、 resolveManagedList()等方法进行具体的解析。

#### 6.2 BeanDefinitionValueResolver#resolveValueIfNecessary 
在完成这个解析过程后，已经为依赖注入准备好了条件，下面的 setPropertyValue() 方法是真正设置属性依赖的地方，该方法的实现是在BeanWrapper的实现类BeanWrapperImpl中，其代码如下:



到了这里，就完成了对Bean各种属性的依赖注入的过程，整个过程我们只要把握住 getBean 这个依赖注入的入口，一步步分析下去，就可以完成对整个依赖注入的把握。


### 7.AbstractAutowireCapableBeanFactory#initializeBean 从该方法可以看到Bean初始化流程

org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
```java
protected Object initializeBean(final String beanName, final Object bean, RootBeanDefinition mbd) {
    // 1. 初始化Aware 方法
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

    // 2. bean 初始化之前, 触发BeanPostProcessor的postProcessBeforeInitialization
    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }

    // 3. 初始化bean
    try {
        this.invokeInitMethods(beanName, wrappedBean, mbd);
    } catch (Throwable var6) {
        Throwable ex = var6;
        throw new BeanCreationException(mbd != null ? mbd.getResourceDescription() : null, beanName, "Invocation of init method failed", ex);
    }
    
    // 4. bean 初始化之后, 触发BeanPostProcessor的postProcessAfterInitialization
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }
    return wrappedBean;
}
```