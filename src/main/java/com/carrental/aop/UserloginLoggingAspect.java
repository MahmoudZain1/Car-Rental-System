package com.carrental.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserloginLoggingAspect {

       Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* com.carrental.service.LoginService.verifyLogin(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        String email = joinPoint.getArgs()[0].toString();
        logger.info("Email {} is attempting to login", email);

        try {
            Object result = joinPoint.proceed();
            if ((Boolean) result) {
                logger.info("Email: {} logged in successfully", email);
            } else {
                logger.info("Email: {} failed to log ", email);
            }
            return result;
        } catch (Throwable e) {
            logger.error("Login attempt failed for email: {} with error: {}", email, e.getMessage());
            throw e;
        }
    }
}