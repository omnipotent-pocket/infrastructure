package com.infrastructure.log.config;

import com.infrastructure.log.aop.AbstractInfrastructureGlobalExceptionAop;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfig {


    @Bean
    @ConditionalOnProperty(prefix = "infra.pocket.exception",name = "default", havingValue = "true",matchIfMissing = true)
    public AbstractInfrastructureGlobalExceptionAop infraPocketGlobalExceptionAop() {
        return new AbstractInfrastructureGlobalExceptionAop.DefaultGlobalExceptionAutoAop();
    }
}
