package com.infrastructure.role.flow.init.spring;

import com.infrastructure.role.flow.enums.RoleModeEnum;
import com.infrastructure.role.flow.factory.DefaultValveFactory;
import com.infrastructure.role.flow.factory.FlowingWaterLineFactory;
import com.infrastructure.role.flow.factory.ValveProduceFactory;
import com.infrastructure.role.flow.intface.RoleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zzh
 * @description
 * @date 2023-06-29 18:28
 */
@Configuration
@Import(MediatorAutoRegister.class)
public class ValveStartConfig {


    @Autowired(required = false)
    private List<RoleProvider> roleProviderList;
    @Autowired
    private List<FlowingWaterLineFactory> flowingWaterLineFactoryList;


    @Bean
    @ConditionalOnMissingBean(ValveProduceFactory.class)
    @ConditionalOnProperty(prefix = "valve.role",name = "mode",havingValue = "STRONG",matchIfMissing = true)
    public ValveProduceFactory strongRoleValveProduceFactory(){
        if(CollectionUtils.isEmpty(roleProviderList)){
            throw new IllegalArgumentException("角色模式为:STRONG时，RoleProvider不能为空");
        }
        Map<String, RoleProvider> roleProviderMap = roleProviderList.stream().collect(Collectors.toMap(RoleProvider::org,Function.identity(),(key1,key2)->key1));
        Map<String, FlowingWaterLineFactory> flowingWaterLineFactoryMap =
                flowingWaterLineFactoryList.stream().collect(Collectors.toMap(FlowingWaterLineFactory::org, Function.identity(),(key1,key2)->key1));
        return new DefaultValveFactory(RoleModeEnum.STRONG,roleProviderMap,flowingWaterLineFactoryMap);
    }
    @Bean
    @ConditionalOnMissingBean(ValveProduceFactory.class)
    @ConditionalOnProperty(prefix = "valve.role",name = "mode",havingValue = "WEAK")
    public ValveProduceFactory weakRoleValveProduceFactory(){
        Map<String, RoleProvider> roleProviderMap = Collections.EMPTY_MAP;
        if(!CollectionUtils.isEmpty(roleProviderList)){
            roleProviderMap = roleProviderList.stream().collect(Collectors.toMap(RoleProvider::org,Function.identity(),(key1,key2)->key1));
        }
        Map<String, FlowingWaterLineFactory> flowingWaterLineFactoryMap =
                flowingWaterLineFactoryList.stream().collect(Collectors.toMap(FlowingWaterLineFactory::org, Function.identity(),(key1,key2)->key1));
        return new DefaultValveFactory(RoleModeEnum.WEAK,roleProviderMap,flowingWaterLineFactoryMap);
    }



}
