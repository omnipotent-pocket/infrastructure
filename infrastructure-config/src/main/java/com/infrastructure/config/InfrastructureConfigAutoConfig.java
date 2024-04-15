package com.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(prefix = "infra.config", name = "enabled", havingValue = "true", matchIfMissing = true)
public class InfrastructureConfigAutoConfig {




    @Configuration
    @ConditionalOnProperty(prefix = "infra.config",name = "type", havingValue = "nacos", matchIfMissing = true)
    public static class NacosConfig{

        @Bean
        public InfrastructureConfigStart start(){
            return new InfrastructureConfigStart();
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "infra.config",name = "type", havingValue = "apollo")
    public static class ApolloConfig{


        @Bean
        public InfrastructureConfigStart start(){
            return new InfrastructureConfigStart();
        }
    }


    public static class InfrastructureConfigStart {

    }
}
