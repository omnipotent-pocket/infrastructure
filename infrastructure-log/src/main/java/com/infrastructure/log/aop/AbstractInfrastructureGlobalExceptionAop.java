package com.infrastructure.log.aop;

import com.infrastructure.common.InfrastructureBaseException;
import com.infrastructure.common.InfrastructureResponseEnum;
import com.infrastructure.common.Result;
import com.infrastructure.log.utils.PrintLogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


public abstract class AbstractInfrastructureGlobalExceptionAop {



    protected Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object res = null;
        try {
            res = proceedingJoinPoint.proceed();
            //打印info日志
            PrintLogUtil.printLog(request, proceedingJoinPoint, res, start);
        } catch (Exception e) {
//             业务异常
            if (e instanceof InfrastructureBaseException) {
                res = Result.error(((InfrastructureBaseException) e).getCode(), ((InfrastructureBaseException) e).getMsg());
                PrintLogUtil.printLog(request, proceedingJoinPoint, res, start);
            }
            else {
                res = Result.error(InfrastructureResponseEnum.SYSTEM_ERROR.getCode(), InfrastructureResponseEnum.SYSTEM_ERROR.getMsg());
                PrintLogUtil.printError(request, proceedingJoinPoint, e, start);
            }
        }
        return res;
    }

    @Aspect
    public static class DefaultGlobalExceptionAutoAop extends AbstractInfrastructureGlobalExceptionAop {

        @Around("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
        public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            return aroundAdvice(proceedingJoinPoint);
        }

    }

}
