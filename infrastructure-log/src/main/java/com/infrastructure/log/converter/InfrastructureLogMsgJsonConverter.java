package com.infrastructure.log.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

public class InfrastructureLogMsgJsonConverter extends ClassicConverter {
    public InfrastructureLogMsgJsonConverter() {
    }

    public String convert(ILoggingEvent event) {
        String target = JSON.toJSONString(event.getFormattedMessage());
        return !StringUtils.isEmpty(target) ? target.replaceAll("\r\n", " ") : "\"\"";
    }
}