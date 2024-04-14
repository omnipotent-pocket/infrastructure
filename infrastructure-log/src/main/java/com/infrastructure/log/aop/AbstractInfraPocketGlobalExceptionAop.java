package com.infrastructure.log.aop;

import com.infrastructure.common.InfraPocketAbstractException;
import com.infrastructure.common.InfrastructureAbsResponseEnum;
import com.infrastructure.common.Result;
import com.infrastructure.log.utils.PrintLogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


public abstract class AbstractInfraPocketGlobalExceptionAop {



    protected Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Object res = null;
        try {
            res = proceedingJoinPoint.proceed();
            //打印info日志
            PrintLogUtil.printLog(request, proceedingJoinPoint, res, start);
        } catch (Exception e) {
//            webErrorPublisher.errorNotify(request, e);
//             业务异常
            if (e instanceof InfraPocketAbstractException) {
                res = Result.error(((InfraPocketAbstractException) e).getCode(), ((InfraPocketAbstractException) e).getMsg());
                //打印info日志，不触发日志告警
                PrintLogUtil.printLog(request, proceedingJoinPoint, res, start);
            }
            // sentinel限流异常
//            else if (e instanceof UndeclaredThrowableException
//                    && ((UndeclaredThrowableException) e).getUndeclaredThrowable() instanceof EdenSentinelException) {
//                res = Result.error(EdenAbstractResponseEnum.RATE_LIMITER.getCode(), ((UndeclaredThrowableException) e).getUndeclaredThrowable().getMessage());
//                //打印info日志，不触发日志告警
//                printAopLog.printLog(request, proceedingJoinPoint, res, start);
//            }
            // 其他异常
            else {
                res = Result.error(InfrastructureAbsResponseEnum.SYSTEM_ERROR.getCode(), InfrastructureAbsResponseEnum.SYSTEM_ERROR.getMsg());
                //打印error日志，触发日志告警
                PrintLogUtil.printError(request, proceedingJoinPoint, e, start);
            }
        }
        return res;
    }

    @Aspect
    public static class DefaultGlobalExceptionAutoAop extends AbstractInfraPocketGlobalExceptionAop {

        @Around("@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController)")
        public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            return aroundAdvice(proceedingJoinPoint);
        }

    }

}
