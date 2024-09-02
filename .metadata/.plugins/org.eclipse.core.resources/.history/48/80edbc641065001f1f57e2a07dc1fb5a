package com.sbs;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 메서드 호출 전에 실행
    @Before("execution(* com.sbs..*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Entering method: " + joinPoint.getSignature().getName() + " of class: " + joinPoint.getSignature().getDeclaringTypeName());
    }

    // 메서드 호출 후에 실행
    @After("execution(* com.sbs..*(..))")
    public void logAfterMethod(JoinPoint joinPoint) {
        logger.info("Exiting method: " + joinPoint.getSignature().getName() + " of class: " + joinPoint.getSignature().getDeclaringTypeName());
    }
}
