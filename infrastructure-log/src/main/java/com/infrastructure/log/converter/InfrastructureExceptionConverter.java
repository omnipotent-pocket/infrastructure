package com.infrastructure.log.converter;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

public class InfrastructureExceptionConverter  extends ThrowableProxyConverter {
    public InfrastructureExceptionConverter() {
    }

    public String convert(ILoggingEvent event) {
        String target = JSON.toJSONString(super.convert(event));
        return !StringUtils.isEmpty(target) ? target.replaceAll("\r\n", " ") : "\"\"";
    }
}
