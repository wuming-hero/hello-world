## Spring使用的常用设计模式
### 工厂模式
Spring使用工厂模式通过 BeanFactory 、 ApplicationContext 创建bean 对象。
抽象Bean工厂类AbstractBeanFactory，很好的实现了该设计模式，createBean方法创建对象的过程放在具体的子类里面实现。
org.springframework.beans.factory.support.AbstractBeanFactory#createBean

### 单例模式
保证一个类仅有一个实例，并提供一个访问它的全局访问点。
spring容器读取bean定义后，根基bean是否为单例类型来实例化对象，如果存在则不再创建

### 代理设计模式  
Spring AOP 功能的实现。

### 适配器模式 
Spring AOP 的增强或通知(Advice)使用到了适配器模式、spring MVC 中也是用到了适配器模式适 配 Controller 。

### 建造者模式
spring容器在构建bean的定义时需要从配置文件里面解析后登记到容器的注册表，BeanDefinitionBuilder扮演建造者的角色来设置BeanDefinition的值。
org.springframework.beans.factory.support.BeanDefinitionBuilder

### 装饰器模式
装饰类和被装饰类可以独立发展，不会相互耦合，装饰模式是继承的一个替代模式，装饰模式可以动态扩展一个实现类的功能。

org.springframework.beans.BeanWrapper
装饰器类，包装了具体的bean，将附带的功能封装在装饰器里面

### 观察者模式
Spring 事件驱动模型就是观察者模式很经典的一个应用。
使用场景：
* 一个抽象模型有两个方面，其中一个方面依赖于另一个方面。将这些方面封装在独立的对象中使它们可以各自独立地改变和复用。
* 一个对象的改变将导致其他一个或多个对象也发生改变，而不知道具体有多少对象将发生改变，可以降低对象之间的耦合度。
* 一个对象必须通知其他对象，而并不知道这些对象是谁。
* 需要在系统中创建一个触发链，A对象的行为将影响B对象，B对象的行为将影响C对象……，可以使用观察者模式创建一种链式触发机制。

观察者模式ApplicationEventPublisher,ApplicationListener

AbstractApplicationContext#publishEvent(java.lang.Object, org.springframework.core.ResolvableType)
```java
protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
    Assert.notNull(event, "Event must not be null");
    if (this.logger.isTraceEnabled()) {
        this.logger.trace("Publishing event in " + this.getDisplayName() + ": " + event);
    }

    Object applicationEvent;
    if (event instanceof ApplicationEvent) {
        applicationEvent = (ApplicationEvent)event;
    } else {
        applicationEvent = new PayloadApplicationEvent(this, event);
        if (eventType == null) {
            eventType = ((PayloadApplicationEvent)applicationEvent).getResolvableType();
        }
    }

    if (this.earlyApplicationEvents != null) {
        this.earlyApplicationEvents.add(applicationEvent);
    } else {
        this.getApplicationEventMulticaster().multicastEvent((ApplicationEvent)applicationEvent, eventType);
    }

    if (this.parent != null) {
        if (this.parent instanceof AbstractApplicationContext) {
            ((AbstractApplicationContext)this.parent).publishEvent(event, eventType);
        } else {
            this.parent.publishEvent(event);
        }
    }

}
```
### 策略模式
代理的实现就是策略模式，默认使用Cglib,如果类有继承，则使用JDK代理。
1. 如果在一个系统里面有许多类，它们之间的区别仅在于它们的行为，那么使用策略模式可以动态地让一个对象在许多行为中选择一种行为。
2. 一个系统需要动态地在几种算法中选择一种。
3. 如果一个对象有很多的行为，如果不用恰当的模式，这些行为就只好使用多重的条件选择语句来实现。

AbstractAutowireCapableBeanFactory#instantiateBean
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