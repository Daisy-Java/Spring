org.springframework.web.servlet.DispatcherServlet#doDispatch
org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter#handle
org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#handleInternal
org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandlerMethod
org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod#invokeAndHandle
org.springframework.web.method.support.InvocableHandlerMethod#invokeForRequest
org.springframework.web.method.support.InvocableHandlerMethod#getMethodArgumentValues
org.springframework.web.method.support.HandlerMethodArgumentResolverComposite#resolveArgument
org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor#resolveArgument，校验也是在此做的
org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor#readWithMessageConverters
org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver#readWithMessageConverters(org.springframework.http.HttpInputMessage, org.springframework.core.MethodParameter, java.lang.reflect.Type)
org.springframework.http.converter.AbstractHttpMessageConverter#read
org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter#readInternal

https://www.cnblogs.com/wyq1995/p/11870220.html


具体校验的逻辑还没看。