package com.infrastructure.config;

import lombok.Getter;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static com.infrastructure.config.AbstractInfraPocketEnvironment.EnvironmentEnum.*;

public abstract class AbstractInfraPocketEnvironment {
    //注入eden默认配置
    private static Properties properties = new Properties();
    //当前环境
    private static String env = StringUtils.isEmpty(System.getProperty("eden.environment")) ? LOCAL.code : System.getProperty("eden.environment");
    //组件环境路劲
    private static String ENV_COMPONENT_PATH;

    private volatile  boolean started = false;

    public AbstractInfraPocketEnvironment(){
        if(properties.isEmpty()){
            init();
        }
    }


    public synchronized void  init(){
        if(!started){
            return;
        }
        PropertyMapper mapper = PropertyMapper.get();
        mapper.from(System.getProperty("eden.environment")).when(p -> p == null || isAlias(p)).to(c -> System.setProperty("eden.environment", env = getByAlias(env).code));


        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(getEnvCommponentPath(env));
            if (resources != null && resources.length > 0) {
                for (Resource resource : resources) {
                    properties.load(resource.getInputStream());
                }
            }
            started = true;
        } catch (IOException e) {

        }
    }


    protected abstract String getEnvCommponentPath(String env);

    public Properties getProperties() {

        return properties;
    }

    enum EnvironmentEnum {
        LOCAL("local", null),
        DEV("dev", new String[]{"developer"}),
        QA("qa", new String[]{"test"}),
        PRE("pre", new String[]{"pre-release","release-pre"}),
        PRO("pro", new String[]{"prod"});
        public final String code;
        @Getter
        private String[] Alias;

        EnvironmentEnum(String code, String[] Alias) {
            this.code = code;
            this.Alias = Alias;
        }

        static boolean isAlias(String alias) {
            boolean b = Arrays.stream(EnvironmentEnum.values()).anyMatch(e -> e.getAlias() != null && Arrays.asList(e.getAlias()).contains(alias));
            return b;
        }

        static EnvironmentEnum getByAlias(String alias) {
            for (EnvironmentEnum e : EnvironmentEnum.values()) {
                if (e.Alias != null && Arrays.asList(e.Alias).contains(alias)) {
                    return e;
                }
            }
            return LOCAL;
        }
    }

    public static class InfraPocketNacosEnvironment extends AbstractInfraPocketEnvironment{

        @Override
        protected String getEnvCommponentPath(String env) {
            return ENV_COMPONENT_PATH == null ? ENV_COMPONENT_PATH = "classpath*:env/" + env + "/nacos*.properties" : ENV_COMPONENT_PATH;
        }
    }
    public static class InfraPocketApolloEnvironment extends AbstractInfraPocketEnvironment{

        @Override
        protected String getEnvCommponentPath(String env) {
            return ENV_COMPONENT_PATH == null ? ENV_COMPONENT_PATH = "classpath*:env/" + env + "/apollo*.properties" : ENV_COMPONENT_PATH;
        }
    }
}
