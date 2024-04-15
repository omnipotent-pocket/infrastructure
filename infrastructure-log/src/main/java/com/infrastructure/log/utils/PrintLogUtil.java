package com.infrastructure.log.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.infrastructure.common.InfrastructureResponseEnum;
import com.infrastructure.common.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

public class PrintLogUtil {



    public static void printLog(HttpServletRequest request, ProceedingJoinPoint proceedingJoinPoint, Object res, long start) {
        MethodSignature ms = (MethodSignature) proceedingJoinPoint.getSignature();
        String printCode = InfrastructureResponseEnum.SUCCESS.getCode();
        String printMsg = InfrastructureResponseEnum.SUCCESS.getMsg();
        Object printRes = res;
        if (res instanceof Result) {
            Result<?> ret = (Result<?>) res;
            printCode = ret.getStatusCode();
            printMsg = ret.getMessage();
        } else if (res instanceof ResponseEntity) {
            printCode = ((ResponseEntity<?>) res).getStatusCode() + "";
            printMsg = ((ResponseEntity<?>) res).getBody().toString();
        }
        LogOutput.infoConvertJson(ms.getName(), "请求响应日志", request, start,
                System.currentTimeMillis(), proceedingJoinPoint.getArgs(), printRes, printCode, printMsg);
    }


    public static void printError(HttpServletRequest request, ProceedingJoinPoint pjp, Exception e, long start) {
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        LogOutput.errorConvertJson(InfrastructureResponseEnum.SYSTEM_ERROR.getCode(),
                ms.getName(), "请求响应异常日志", request, start, System.currentTimeMillis(), pjp.getArgs(), e);
    }



    public static class LogOutput {
        private static Logger logger = LoggerFactory.getLogger(LogOutput.class);
        private static String localFQCN = LogOutput.class.getName();

        public LogOutput() {
        }

        public static void infoConvertJson(String methodName, String logMessage, HttpServletRequest request, long startTime, long endTime, Object requestData, Object responseData, String statusCode, String responseMsg) {
            try {
//                RequestHeaderInfo requestHeaderInfo = new RequestHeaderInfo(request);日志输出
//                LogEntity entity = LogEntity.builder().statusCode(statusCode).trackingNo(LogThreadLocal.getTrackingNo()).elapsedTime(endTime - startTime).url(RequestUtils.getUrl(request)).request(requestData).response(responseData).logMessage(logMessage).methodName(methodName).classPath(LogbackUtil.message(localFQCN)).startTime(startTime).endTime(endTime).userAgent(RequestUtils.getUserAgent(request)).requestIp(RequestUtils.getRequestIp(request)).realIp(RequestUtils.getRemoteAddr(request)).serverIp(IpUtil.getLocalIP()).tId(requestHeaderInfo.getTId()).memberId(requestHeaderInfo.getMemberId()).page(requestHeaderInfo.getPage()).appVersion(requestHeaderInfo.getAppVersion()).sessionId(requestHeaderInfo.getSessionId()).build();
                MDC.put("messageData", JSONObject.toJSONString(null, new SerializerFeature[]{SerializerFeature.IgnoreNonFieldGetter}));
                logger.info(responseMsg);
            } catch (Exception var16) {
                Exception e = var16;
                logger.error("printException:{}", JSON.toJSONString(e));
            } finally {
                MDC.remove("messageData");
            }

        }


        public static void errorConvertJson(String statusCode, String methodName, String message, HttpServletRequest request, long startTime, long endTime, Object requestData, Exception ex) {
            try {
                String exception = JSON.toJSONString(ex);
//                RequestHeaderInfo requestHeaderInfo = new RequestHeaderInfo(request);
//                LogEntity.LogEntityBuilder builder = LogEntity.builder();
//                LogEntity logEntity = builder.statusCode(statusCode).trackingNo(LogThreadLocal.getTrackingNo()).elapsedTime(endTime - startTime).url(RequestUtils.getUrl(request)).request(requestData).exception(exception).methodName(methodName).classPath(LogbackUtil.message(localFQCN)).startTime(startTime).endTime(endTime).userAgent(RequestUtils.getUserAgent(request)).requestIp(RequestUtils.getRequestIp(request)).realIp(RequestUtils.getRemoteAddr(request)).serverIp(IpUtil.getLocalIP()).tId(requestHeaderInfo.getTId()).memberId(requestHeaderInfo.getMemberId()).page(requestHeaderInfo.getPage()).appVersion(requestHeaderInfo.getAppVersion()).sessionId(requestHeaderInfo.getSessionId()).build();
                MDC.put("messageData", JSONObject.toJSONString(null, new SerializerFeature[]{SerializerFeature.IgnoreNonFieldGetter}));
                logger.error(message);
            } catch (Exception var17) {
                Exception e = var17;
                logger.error("printException:{}", JSON.toJSONString(e));
            } finally {
                MDC.remove("messageData");
            }

        }




    }

}
