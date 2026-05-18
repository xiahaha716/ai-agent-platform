package com.example.aiagentplatform.config;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest; // 注意：Spring Boot 3 使用的是 jakarta 包

@Aspect
@Component
public class WebLogAspect {

    private static final Logger log = LoggerFactory.getLogger(WebLogAspect.class);

    // 切点：拦截 controller 包下的所有公有方法
    @Pointcut("execution(public * com.example.aiagentplatform.controller..*.*(..))")
    public void webLog() {}

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String url = request != null ? request.getRequestURI() : "Unknown URL";

        // 打印请求开始日志
        log.info(">>> 开始请求 | URL: {} | 方法: {}.{}", url, className, methodName);

        // 执行真实的目标方法 (也就是 controller 里的逻辑)
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            // 如果方法抛出异常，这里也会记录时间，然后继续往外抛，交给 GlobalExceptionHandler 处理
            long timeTaken = System.currentTimeMillis() - startTime;
            log.error("<<< 请求异常 | URL: {} | 耗时: {} ms", url, timeTaken);
            throw e;
        }

        // 打印请求结束及耗时日志
        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("<<< 请求结束 | URL: {} | 耗时: {} ms", url, timeTaken);

        return result;
    }
}
