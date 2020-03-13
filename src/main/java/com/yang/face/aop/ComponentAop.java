package com.yang.face.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author wanyifan
 * @date 2020/1/15 9:04
 */
@Component
@Aspect
public class ComponentAop {
    private static final Logger logger = LoggerFactory.getLogger(ComponentAop.class);

    @Pointcut("execution(* com.yang.face.*..*(..))")
    public void comAop() {
    }

    @Around("comAop()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object res = null;
        long time = System.currentTimeMillis();
        try {
            res = joinPoint.proceed();
        } catch (Exception e) {
            //方法执行完成后增加日志
            logger.error(e.getMessage(), e);
        } finally {
            time = System.currentTimeMillis() - time;
            logger.error("{} 耗时：{}", joinPoint.getTarget().getClass().getName(), time);

        }
        return res;
    }
}
