# Spring初始化过程
Spring容器（BeanFactory）按照scope将bean划分成多种类型，例如singleton、prototype、request、session等等，最常见的就是singleton和prototype这两种，
前者所标识的bean是一个单例对象，在相同的上下文中（ApplicationContext）每次getBean()请求拿到的都会是同一份实例，
prototype的不同之处在于每次getBean()拿到的都是一个fresh new instance。


org.springframework.context.support.AbstractApplicationContext#refresh
```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        // Prepare this context for refreshing.
        prepareRefresh();

        // 1. 获得新的BeanFactory
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // Prepare the bean factory for use in this context.
        prepareBeanFactory(beanFactory);

        try {
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);

            // 2. 处理BeanPostProcessor
            invokeBeanFactoryPostProcessors(beanFactory);

            // Register bean processors that intercept bean creation.
            registerBeanPostProcessors(beanFactory);

            // 3. 注册处理消息和事件
            initMessageSource();

            // Initialize event multicaster for this context.
            initApplicationEventMulticaster();

            // Initialize other special beans in specific context subclasses.
            onRefresh();

            // Check for listener beans and register them.
            registerListeners();
            
            // Instantiate all remaining (non-lazy-init) singletons.
            // 4. 完成对象实例化入口
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


## 容器初始化过程 
这里我们以ClassPathXmlApplicationContext为例来介绍下容器的初始化过程，所有初始化逻辑都是在refresh()方法的执行流程中展开的，下面会对比较关键的几个环节进行介绍。

### 1. BeanFactory的刷新
刷新工厂 org.springframework.context.support.AbstractApplicationContext#obtainFreshBeanFactory

首先是BeanFactory的刷新。BeanFactory会将旧的工厂对象连同其管理的所有bean definition以及实例全部销毁掉，然后创建一个新的工厂对象，重新从xml配置文件中读取bean definition信息，借助BeanDefinitionRegistry接口将所有bean的配置信息注册到BeanFactory之中


### 2. 处理BeanFactoryPostProcessor
AbstractApplicationContext会调用Spring内部的各种BeanFactoryPostProcessor来对上一步刷新得到的BeanFactory做进一步的配置。需要注意区分BeanFactoryPostProcessor与BeanPostProcessor的差别。
首先，两者的作用不同，
* BeanFactoryPostProcessor会在所有bean实例化之前对BeanFactory进行修改，比如向容器中注册一些仅供框架内部使用的bean，
* BeanPostProcessor只能对当前正在实例化的某一个bean进行处理，比如用代理（proxy）来替换原始的bean实例；
* 二者的调用时机不同，BeanFactoryPostProcessor仅会在容器初始化的过程中被调用一次，BeanPostProcessor会在bean每次实例化的时候被调用。 在BeanFactoryPostProcessor介入容器初始化的过程中，Spring会按照一定顺序对其进行调用，首先调用所有实现了

### 3. AbstractApplicationContext会对其他组件进行初始化工作
例如注册event multicaster、注册event listener、提前实例化non-lazy singleton bean等等（后面章节会对bean实例化过程进行介绍）.至此，Spring容器初始化工作全部完成。