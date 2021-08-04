初始化流程：
org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.postProcessAfterInitialization，bean后处理
org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.wrapIfNecessary
org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.createProxy
    => ProxyFactory proxyFactory = new ProxyFactory(); 实例化代理工厂
    => org.springframework.aop.framework.ProxyFactory.getProxy(java.lang.ClassLoader) 创建代理对象
        => org.springframework.aop.framework.JdkDynamicAopProxy.getProxy(java.lang.ClassLoader) JDK动态代理


执行流程：
TransactionInterceptor.invoke
    => TransactionAspectSupport.invokeWithinTransaction
        => TransactionAttributeSource tas = getTransactionAttributeSource(); // 查询事务信息
        => org.springframework.transaction.interceptor.TransactionAspectSupport.createTransactionIfNecessary // 获取事务
            => AbstractPlatformTransactionManager.getTransaction // 获取事务
                => org.springframework.transaction.jta.JtaTransactionManager.doGetTransaction
                => AbstractPlatformTransactionManager.handleExistingTransaction，处理已存在事务的情况
                => AbstractPlatformTransactionManager.suspend，挂起事务，也叫新建事务，我猜测，这里就是requires_new传播机制执行的地方，在这里try...catch
        try {
           invocation.proceedWithInvocation();     // 执行目标方法
        } catch (Throwable ex) {
           completeTransactionAfterThrowing(txInfo, ex);   // 回滚
        } finally {
           cleanupTransactionInfo(txInfo);     // 清理事务信息
        }
        commitTransactionAfterReturning(txInfo);     // 提交事务




