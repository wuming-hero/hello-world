单例设计模式 : Spring 中的 Bean 默认都是单例的。
工厂设计模式 : Spring使用工厂模式通过 BeanFactory 、 ApplicationContext 创建bean 对象。
代理设计模式 : Spring AOP 功能的实现。
观察者模式: Spring 事件驱动模型就是观察者模式很经典的一个应用。
适配器模式: Spring AOP 的增强或通知(Advice)使用到了适配器模式、spring MVC 中也是用到了适配器模式适 配 Controller 。
策略模式：代理的实现就是策略模式，默认使用Cglib,如果类有继承，则使用JDK代理。