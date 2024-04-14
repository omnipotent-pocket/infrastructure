package com.infrastructure.role.flow.factory;

import com.infrastructure.role.flow.enums.RoleModeEnum;
import com.infrastructure.role.flow.intface.RoleProvider;

import java.util.*;

public class DefaultValveFactory extends AbstractValveProduceFactory{

    public DefaultValveFactory(RoleModeEnum roleMode, Map<String, RoleProvider> roleProviderMap, Map<String, FlowingWaterLineFactory> flowingWaterLineFactoryMap) {
        super(roleMode, roleProviderMap, flowingWaterLineFactoryMap);
    }
}
