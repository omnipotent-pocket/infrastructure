package com.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(prefix = "infra.pocket.config", name = "enabled", havingValue = "true", matchIfMissing = true)
public class InfrastructurePocketConfigAutoConfig {




    @Configuration
    @ConditionalOnProperty(prefix = "infra.pocket.config",name = "type", havingValue = "nacos", matchIfMissing = true)
    public static class NacosConfig{

        @Bean
        public InfrastructurePocketConfigStart start(){
            return new InfrastructurePocketConfigStart();
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "infra.pocket.config",name = "type", havingValue = "apollo")
    public static class ApolloConfig{


        @Bean
        public InfrastructurePocketConfigStart start(){
            return new InfrastructurePocketConfigStart();
        }
    }


    public static class InfrastructurePocketConfigStart{

    }
}
