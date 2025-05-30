面试官问我：三级缓存可以解决循环依赖的问题，那两级缓存可以解决Spring的循环依赖问题么？是不是无法解决代理对象的问题？

## 什么是Spring的循环依赖？
循环依赖是指两个或多个Bean在初始化时互相依赖，形成了一个闭环。例如：
* Bean A 依赖 Bean B
* Bean B 也依赖 Bean A
在Spring中，这种情况常见于构造器注入、字段注入或setter注入。如果没有适当的机制，循环依赖会导致Spring容器在初始化Bean时陷入死循环，最终抛出 BeanCurrentlyInCreationException。

Spring通过三级缓存（Three-Level Cache）解决了大部分循环依赖问题，尤其是在单例作用域（Singleton Scope）下。那么，三级缓存到底是什么？我们先来回顾一下。

## Spring的三级缓存
Spring的 DefaultSingletonBeanRegistry 中定义了三级缓存，用于管理Bean的创建和循环依赖的解决：

1. 一级缓存（singletonObjects）：存储已经完全初始化的单例Bean，键是Bean的名称，值是Bean实例。
2. 二级缓存（earlySingletonObjects）：存储早期暴露的Bean实例，这些Bean已经实例化但尚未完成属性填充和初始化（即未调用 init 方法）。
3. 三级缓存（singletonFactories）：存储的是 ObjectFactory，它是一个函数式接口，能够动态生成Bean的早期引用，通常用于处理代理对象（如AOP代理）。

三级缓存的工作流程如下：

1. 当Spring创建Bean A时，首先实例化A（调用构造器），然后将其 ObjectFactory 放入三级缓存。
2. 在填充A的属性时，发现A依赖B，于是开始创建B。
3. 同样，B实例化后将其 ObjectFactory 放入三级缓存。
4. B在填充属性时发现依赖A，此时Spring会从三级缓存中获取A的 ObjectFactory，通过它生成A的早期引用（可能是代理对象），并将该引用放入二级缓存。
5. B完成初始化后，放入一级缓存。
6. A继续完成属性填充和初始化，最终也放入一级缓存。

通过这种机制，Spring不仅解决了循环依赖，还能正确处理AOP代理对象，因为三级缓存中的 ObjectFactory 可以在需要时生成动态代理。

## 问题来了：两级缓存可以解决循环依赖吗？
现在，假设我们去掉三级缓存，只保留一级缓存（singletonObjects）和二级缓存（earlySingletonObjects），Spring还能否解决循环依赖？答案是：可以解决部分循环依赖，但无法处理涉及代理对象的场景。下面我们来详细分析。

### 两级缓存的工作方式
在只有两级缓存的情况下，Spring的Bean创建流程可以简化为：

1. 创建Bean A，实例化后将A的早期引用（未完成属性填充的实例）直接放入二级缓存。
2. A在填充属性时发现依赖B，开始创建B。
3. 同样，B实例化后将其早期引用放入二级缓存。
4. B在填充属性时发现依赖A，直接从二级缓存中获取A的早期引用。
5. B完成初始化，放入一级缓存。
6. A继续完成初始化，最终也放入一级缓存。

从这个流程看，两级缓存可以解决普通的循环依赖问题，即没有AOP代理或特殊后处理的情况。因为二级缓存提前暴露了Bean的早期引用，打破了循环依赖的死锁。

### 两级缓存的局限性：代理对象问题
然而，当涉及到代理对象（如通过AOP生成的动态代理）时，两级缓存就显得力不从心了。原因在于：

* 代理对象的创建时机：在Spring中，AOP代理（如CGLIB或JDK动态代理）通常在Bean完成属性填充和初始化后，通过 BeanPostProcessor（如 AnnotationAwareAspectJAutoProxyCreator）生成。这意味着早期暴露的Bean实例（放入二级缓存的）是一个原始对象，而不是代理对象。
* 循环依赖中的不一致性：假设Bean A被AOP代理，B依赖A。在两级缓存机制下，B从二级缓存中获取的是A的原始对象引用，而不是A的代理对象。后续即使A完成了初始化并生成了代理对象，B中注入的仍然是A的原始对象。这会导致以下问题：
  * 如果AOP为A添加了增强逻辑（如事务、日志），B中引用的A不会具备这些增强功能，违背了AOP的设计预期。
  * 如果代码中通过一级缓存获取A，会得到代理对象，而B中持有的却是原始对象，导致对象引用不一致，可能引发运行时异常。

在三级缓存中，ObjectFactory 的存在解决了这个问题。ObjectFactory 可以在生成早期引用时动态决定是否需要创建代理对象（通过调用 getEarlyBeanReference 方法）。这样，B获取到的A引用已经是代理对象，保证了引用的一致性和AOP逻辑的正确性。

### 两级缓存为何无法优雅处理代理对象？
为了更直观地说明，我们来看一个例子：
```java
@Component
public class ServiceA {
    
    @Autowired
    private ServiceB serviceB;
    
        public void doSomething() {
            System.out.println("ServiceA doing something");
        }
    }
    
    @Component
    @Aspect
    public class ServiceB {
    @Autowired
    private ServiceA serviceA;
    
        public void doSomething() {
            System.out.println("ServiceB doing something");
        }
    }
    
    @Aspect
    @Component
    public class LoggingAspect {
    @Around("execution(* com.example..*.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("Before method execution");
        Object result = joinPoint.proceed();
        System.out.println("After method execution");
        return result;
    }
}
```

在这个例子中：

* ServiceA 和 ServiceB 互相依赖，形成循环依赖。
* ServiceA 和 ServiceB 都被AOP代理（通过 LoggingAspect 增强）。


#### 在三级缓存机制下：
1. 创建 ServiceA，实例化后将其 ObjectFactory 放入三级缓存。
2. 填充 ServiceA 属性时发现依赖 ServiceB，开始创建 ServiceB。
3. ServiceB 实例化后将其 ObjectFactory 放入三级缓存。
4. 填充 ServiceB 属性时发现依赖 ServiceA，从三级缓存获取 ServiceA 的 ObjectFactory，通过 getEarlyBeanReference 生成 ServiceA 的代理对象（带AOP增强）。
5. ServiceB 完成初始化，放入一级缓存。
6. ServiceA 继续完成初始化，生成代理对象，放入一级缓存。

最终，ServiceB 中注入的 ServiceA 和一级缓存中的 ServiceA 都是同一个代理对象，AOP增强逻辑正常生效。

#### 在两级缓存机制下：

1. 创建 ServiceA，实例化后将其原始对象放入二级缓存。
2. 填充 ServiceA 属性时发现依赖 ServiceB，开始创建 ServiceB。
3. ServiceB 实例化后将其原始对象放入二级缓存。
4. 填充 ServiceB 属性时发现依赖 ServiceA，从二级缓存获取 ServiceA 的原始对象（非代理对象）。
5. ServiceB 完成初始化，生成代理对象，放入一级缓存。
6. ServiceA 完成初始化，生成代理对象，放入一级缓存。


结果是：
* ServiceB 中注入的 ServiceA 是原始对象，没有AOP增强。
* 一级缓存中的 ServiceA 是代理对象。
* 这导致 ServiceB 调用 serviceA.doSomething() 时，不会触发 LoggingAspect 的日志逻辑，行为不一致。


## 两级缓存的适用场景
尽管两级缓存无法处理代理对象的循环依赖，它在某些场景下仍然是可行的：

1. 没有AOP或代理的场景：如果项目中不使用AOP（如Spring AOP或AspectJ），或者Bean没有被任何 BeanPostProcessor 包装为代理对象，两级缓存足以解决循环依赖。
2. 简单的setter注入：两级缓存适用于通过setter方法或字段注入的循环依赖，因为这些场景只需要早期引用即可。
3. 性能优化需求：三级缓存的 ObjectFactory 引入了额外的复杂性和性能开销。如果明确知道项目中不需要代理对象，可以通过简化缓存机制来提高性能（不过Spring默认不提供这种配置）。

https://juejin.cn/post/7491984282538934311