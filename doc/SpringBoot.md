@SpringBootApplication(scanBasePackages = {"cn.iocoder.mall.admin"})
@EnableAsync(proxyTargetClass = true)
@MapperScan("cn.iocoder.mall.admin.dao")
@EnableTransactionManagement(proxyTargetClass = true)
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}

1. 初始化 
public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
   this.resourceLoader = resourceLoader;
   Assert.notNull(primarySources, "PrimarySources must not be null");
   this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
   // 推断应用的类型：创建的是 REACTIVE应用、SERVLET应用、NONE 三种中的某一种, 工作中用到了SERVLET
   this.webApplicationType = WebApplicationType.deduceFromClasspath();
   // 使用 SpringFactoriesLoader查找并加载 classpath下 META-INF/spring.factories文件中所有可用的 ApplicationContextInitializer
   setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
   // 使用 SpringFactoriesLoader查找并加载 classpath下 META-INF/spring.factories文件中的所有可用的 ApplicationListener
   setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
   // 推断并设置 main方法的定义类(<啥东西>)
   this.mainApplicationClass = deduceMainApplicationClass();
}
2. run
   public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
   return new SpringApplication(primarySources).run(args);
   }


/**
* Run the Spring application, creating and refreshing a new
* {@link ApplicationContext}.
* @param args the application arguments (usually passed from a Java main method)
* @return a running {@link ApplicationContext}
  */
  public ConfigurableApplicationContext run(String... args) {
  StopWatch stopWatch = new StopWatch();
  stopWatch.start();
  ConfigurableApplicationContext context = null;
  Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
  configureHeadlessProperty();
  // 1. 通过 SpringFactoriesLoader 加载 META-INF/spring.factories 文件，获取并创建 SpringApplicationRunListener 对象
  SpringApplicationRunListeners listeners = getRunListeners(args);
  // 2. 由 SpringApplicationRunListener 来发出 starting 消息
  listeners.starting();
  try {
  // 3. 创建参数，并配置当前 SpringBoot 应用将要使用的 Environment
  ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
  // 4. 完成之后，依然由 SpringApplicationRunListener 来发出 environmentPrepared 消息
  ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
  configureIgnoreBeanInfo(environment);
  Banner printedBanner = printBanner(environment);
  // 5. 创建 ApplicationContext
  context = createApplicationContext();
  exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class, new Class[] { 				ConfigurableApplicationContext.class }, context);
  // 6. 初始化ApplicationContext设置Environment加载相关配置等, 并
    1. 由 SpringApplicationRunListener 来发出contextPrepared 消息，告知SpringBoot 应用使用的 ApplicationContext 已准备OK
    2. 将各种 beans 装载入 ApplicationContext，继续由 SpringApplicationRunListener 来发出 contextLoaded 消息，告知 SpringBoot 应用使用的 ApplicationContext 已装填OK
       prepareContext(context, environment, listeners, applicationArguments, printedBanner);
       // 7. refresh ApplicationContext，完成IoC容器可用的最后一步
       refreshContext(context);
       afterRefresh(context, applicationArguments);
       stopWatch.stop();
       if (this.logStartupInfo) {
       new StartupInfoLogger(this.mainApplicationClass)
       .logStarted(getApplicationLog(), stopWatch);
       }
       // 8. 由 SpringApplicationRunListener 来发出 started 消息
       listeners.started(context);
       // 9. 完成最终的程序的启动
       callRunners(context, applicationArguments);
       }
       catch (Throwable ex) {
       handleRunFailure(context, ex, exceptionReporters, listeners);
       throw new IllegalStateException(ex);
       }

  try {
  // 10. 由 SpringApplicationRunListener 来发出 running 消息，告知程序已运行起来了
  listeners.running(context);
  }
  catch (Throwable ex) {
  handleRunFailure(context, ex, exceptionReporters, null);
  throw new IllegalStateException(ex);
  }
  return context;
  }
