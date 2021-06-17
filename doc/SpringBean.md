Bean生命周期
1.执行InstantiationAwareBeanPostprocessor的postProcessBeforeInstantiation()    // Instantiation 实例化
2.执行bean构造器
3.执行InstantiationAwareBeanPostprocessor的postProcessPropertyValues()
4.对bean属性进行依赖注入
5.各种aware接口
如果bean实现了BeanNameAware接口，spring将bean的id传给setBeanName()方法；
BeanClassLoaderAware ,setBeanClassLoader()；
如果bean实现了BeanFactoryAware接口，spring将调用setBeanFactory方法，将BeanFactory实例传进来；
EnvironmentAware ,setEnvironment()；
EmbeddedValueResolverAware ,setEmbeddedValueResolver()；
ResourceLoaderAware ,setResourceLoader()；
ApplicationEventPublisherAware ,setApplicationEventPublisher();
MessageSourceAware ,setMessageSource();
如果bean实现了ApplicationContextAware接口，它的setApplicationContext()方法将被调用，将应用上下文的引用传入到bean中；
如果是WebApplication，ServeltContextAware，setServeltContext();
6.如果bean实现了BeanPostProcessor接口，调用postProcessBeforeInitialization方法；
7.如果bean实现了InitializingBean接口，调用afterPropertiesSet方法，类似的如果bean使用了init-method属性声明了初始化方法，该方法也会被调用；
8.如果bean实现了BeanPostProcessor接口，调用postProcessAfterInitialization方法；
9.此时bean已经准备就绪，可以被应用程序使用了，他们将一直驻留在应用上下文中，直到该应用上下文被销毁；


加载单例Bean
org.springframework.context.support.AbstractApplicationContext#refresh

org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization
org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons（实例化bean）
org.springframework.beans.factory.support.AbstractBeanFactory#getBean(java.lang.String)
org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean
org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean
org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean
org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance（构造bean）
org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean（初始化bean）


MVC
org.apache.catalina.core.ApplicationDispatcher#forward
org.apache.catalina.core.ApplicationDispatcher#doForward
org.apache.catalina.core.ApplicationDispatcher#processRequest
org.apache.catalina.core.ApplicationDispatcher#invoke
org.apache.catalina.core.ApplicationFilterChain#doFilter
org.apache.catalina.core.ApplicationFilterChain#internalDoFilter

javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)

org.springframework.web.servlet.FrameworkServlet#doPost
org.springframework.web.servlet.FrameworkServlet#processRequest
org.springframework.web.servlet.DispatcherServlet#doService


// 请求分发
org.springframework.web.servlet.DispatcherServlet#doDispatch(){
// 从handlerMapping中获取处理器
HandlerExecutionChain mappedHandler = getHandler(processedRequest);
-> handlerMapping.getHandler(request);
// 获取处理器适配器，一般是RequestMappingHandlerAdapter
HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
-> ha = RequestMappingHandlerAdapter.java
// 循环拦截器执行拦截器的preHandle方法
mappedHandler.applyPreHandle(processedRequest, response);
-> interceptor.preHandle(request, response, this.handler);
// 执行处理器
ModelAndView mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
-> org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter#handle
-> org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#handleInternal(){
// 调用控制层方法
org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod
// 根据该发生器的设置准备给定的响应
org.springframework.web.servlet.support.WebContentGenerator#prepareResponse
}
// 倒序循环拦截器执行拦截器的postHandle方法
mappedHandler.applyPostHandle(processedRequest, response, mv);
-> interceptor.postHandle(request, response, this.handler, mv);
}

事务
// AbstractAutoProxyCreator.class
@Override
public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
if (bean != null) {
Object cacheKey = getCacheKey(bean.getClass(), beanName);
if (!this.earlyProxyReferences.contains(cacheKey)) {
// 创建代理对象
return wrapIfNecessary(bean, beanName, cacheKey);
}
}
return bean;
}
​
protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
// 参数检查，跳过已经执行过代理对象生成，或者已知的不需要生成代理对象的Bean
...
​
// Create proxy if we have advice.
// 查询当前Bean所有的AOP增强配置，最终是通过AOPUtils工具类实现
Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
if (specificInterceptors != DO_NOT_PROXY) {
this.advisedBeans.put(cacheKey, Boolean.TRUE);
// 执行AOP织入，创建代理对象
Object proxy = createProxy(
bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
this.proxyTypes.put(cacheKey, proxy.getClass());
return proxy;
}
​
this.advisedBeans.put(cacheKey, Boolean.FALSE);
return bean;
}
​
protected Object createProxy(Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {
​
   if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
       AutoProxyUtils.exposeTargetClass((ConfigurableListableBeanFactory) this.beanFactory, beanName, beanClass);
  }
​
   // 实例化代理工厂类
   ProxyFactory proxyFactory = new ProxyFactory();
   proxyFactory.copyFrom(this);
​
   // 当全局使用动态代理时，设置是否需要对目标Bean强制使用CGLIB动态代理
  ...
​
   // 构建AOP增强顾问，包含框架公共增强和应用程序自定义增强
   // 设置proxyFactory属性，如增强、目标类、是否允许变更等
  ...
​
   // 创建代理对象
   return proxyFactory.getProxy(getProxyClassLoader());
}
最后是通过调用ProxyFactory#getProxy(java.lang.ClassLoader)方法来创建代理对象。
// ProxyFactory.class
public Object getProxy(ClassLoader classLoader) {
   return createAopProxy().getProxy(classLoader);
}
​
// ProxyFactory父类ProxyCreatorSupport.class
protected final synchronized AopProxy createAopProxy() {
   if (!this.active) {
       activate();
  }
   return getAopProxyFactory().createAopProxy(this);
}
​
public ProxyCreatorSupport() {
   this.aopProxyFactory = new DefaultAopProxyFactory();
}
ProxyFactory的父类构造器实例化了DefaultAopProxyFactory类，从其源代码我们可以看到Spring动态代理方式选择策略的实现：如果目标类optimize，proxyTargetClass属性设置为true或者未指定需要代理的接口，则使用CGLIB生成代理对象，否则使用JDK动态代理。
public class DefaultAopProxyFactory implements AopProxyFactory, Serializable {
   @Override
   public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
       // 如果optimize，proxyTargetClass属性设置为true或者未指定代理接口，则使用CGLIB生成代理对象
       if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
           Class<?> targetClass = config.getTargetClass();
// 参数检查，targetClass为空抛出异常
...
// 目标类本身是接口或者代理对象，仍然使用JDK动态代理
if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
return new JdkDynamicAopProxy(config);
}
// Objenesis是一个可以不通过构造器创建子类的java工具类库
// 作为Spring 4.0后CGLIB的默认实现
return new ObjenesisCglibAopProxy(config);
}
else {
// 否则使用JDK动态代理
return new JdkDynamicAopProxy(config);
}
}
...
}


		// TransactionInterceptor.class
		@Override
		public Object invoke(final MethodInvocation invocation) throws Throwable {
		   // 获取targetClass
		  ...
		​
		   // Adapt to TransactionAspectSupport's invokeWithinTransaction...
		   return invokeWithinTransaction(invocation.getMethod(), targetClass, new InvocationCallback() {
		       @Override
		       public Object proceedWithInvocation() throws Throwable {
		           // 实际执行目标方法
		           return invocation.proceed();
		      }
		  });
		}
		​
		// TransactionInterceptor父类TransactionAspectSupport.class
		protected Object invokeWithinTransaction(Method method, Class<?> targetClass, final InvocationCallback invocation)
		       throws Throwable {
		​
		   // If the transaction attribute is null, the method is non-transactional.
		   // 查询目标方法事务属性、确定事务管理器、构造连接点标识（用于确认事务名称）
		   final TransactionAttribute txAttr = getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
		   final PlatformTransactionManager tm = determineTransactionManager(txAttr);
		   final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);
		​
		   if (txAttr == null || !(tm instanceof CallbackPreferringPlatformTransactionManager)) {
		       // 事务获取
		       TransactionInfo txInfo = createTransactionIfNecessary(tm, txAttr, joinpointIdentification);
		       Object retVal = null;
		       try {
		           // 通过回调执行目标方法
		           retVal = invocation.proceedWithInvocation();
		      }
		       catch (Throwable ex) {
		           // 目标方法执行抛出异常，根据异常类型执行事务提交或者回滚操作
		           completeTransactionAfterThrowing(txInfo, ex);
		           throw ex;
		      }
		       finally {
		           // 清理当前线程事务信息
		           cleanupTransactionInfo(txInfo);
		      }
		       // 目标方法执行成功，提交事务
		       commitTransactionAfterReturning(txInfo);
		       return retVal;
		  } else {
		       // 带回调的事务执行处理，一般用于编程式事务
		      ...
		  }
		}
		//TransactionAspectSupport.class
		protected TransactionInfo createTransactionIfNecessary(
		       PlatformTransactionManager tm, TransactionAttribute txAttr, final String joinpointIdentification) {
		  ...
		   TransactionStatus status = null;
		   if (txAttr != null) {
		       if (tm != null) {
		           // 获取事务
		           status = tm.getTransaction(txAttr);
		          ...
		}
		​
		protected void commitTransactionAfterReturning(TransactionInfo txInfo) {
		   if (txInfo != null && txInfo.hasTransaction()) {
		      ...
		       // 提交事务
		       txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
		  }
		}
		​
		protected void completeTransactionAfterThrowing(TransactionInfo txInfo, Throwable ex) {
		   if (txInfo != null && txInfo.hasTransaction()) {
		      ...
		       if (txInfo.transactionAttribute.rollbackOn(ex)) {
		           try {
		               // 异常类型为回滚异常，执行事务回滚
		               txInfo.getTransactionManager().rollback(txInfo.getTransactionStatus());
		          }
		          ...
		      } else {
		           try {
		               // 异常类型为非回滚异常，仍然执行事务提交
		               txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
		          }
		          ...
		}
		​
		protected final class TransactionInfo {
		   private final PlatformTransactionManager transactionManager;
		  ...

**/