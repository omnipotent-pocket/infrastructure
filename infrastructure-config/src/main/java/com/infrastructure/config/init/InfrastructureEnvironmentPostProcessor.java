package com.infrastructure.config.init;

import cn.hutool.core.util.StrUtil;
import com.infrastructure.config.AbstractInfrastructureEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

public class InfrastructureEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //防止bootstrap加载，影响apollo初始化
        if (application.getWebApplicationType().equals(WebApplicationType.SERVLET)) {

            String property = environment.getProperty("infra.pocket.config.type");
            String enabled = environment.getProperty("infra.pocket.config.enabled");
            if(StrUtil.isEmpty(property) || !Boolean.valueOf(enabled)){
                return;
            }
            if (StrUtil.isEmpty(property)) {
                throw new IllegalArgumentException("infra.pocket.config.type is null");
            }
            PropertiesPropertySource propertiesPropertySource;
            if("nacos".equals(property)){
                propertiesPropertySource = new PropertiesPropertySource("infraNacosEnvironment", new AbstractInfrastructureEnvironment.InfrastructureNacosEnvironment().getProperties());
            } else {
                propertiesPropertySource = new PropertiesPropertySource("infraApolloEnvironment", new AbstractInfrastructureEnvironment.InfrastructureApolloEnvironment().getProperties());
            }
            environment.getPropertySources().addLast(propertiesPropertySource);
        }
    }
}
