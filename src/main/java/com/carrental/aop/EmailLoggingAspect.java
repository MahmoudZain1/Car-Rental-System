package com.carrental.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EmailLoggingAspect {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Around("execution(* com.carrental.service.MailSenderService.sendEmail(..))")
    public  Object log(ProceedingJoinPoint joinPoint){

        logger.info("preparing to send email : " + joinPoint.getSignature());

        Object [] args = joinPoint.getArgs();
        Object res;

        try{
             res = joinPoint.proceed();
            logger.info("Email sent successfully to : " + args[0]);
        } catch (Throwable e) {
            logger.error("Error sent Email" + e.getMessage());
            throw new RuntimeException(e);
        }

        return res;
    }
}
