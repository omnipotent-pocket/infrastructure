package com.infrastructure.role.flow.factory;

import com.infrastructure.role.flow.enums.RoleModeEnum;
import com.infrastructure.role.flow.intface.FlowingWaterLine;
import com.infrastructure.role.flow.intface.RoleProvider;
import com.infrastructure.role.flow.intface.ValveRole;
import com.infrastructure.role.flow.pipeline.RoleFlowPipeline;
import com.infrastructure.role.flow.valves.Valve;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 */
public abstract class AbstractValveProduceFactory implements ValveProduceFactory{

    private static Map<String, ConcurrentHashMap<ValveRole, List<Valve>>> CACHE_ORG_FLOWING_WATER_ROLE_VALVE_MAP = new ConcurrentHashMap<>();

    private static Map<String,ConcurrentHashMap<FlowingWaterLine, List<Valve>>> CACHE_ORG_FLOWING_WATER_VALVE_MAP = new ConcurrentHashMap<>();

    private static final Map<String,FlowingWaterLineFactory> FLOWING_WATER_LINE_FACTORY_MAP = new HashMap<>();

    private static final Map<String,Map<FlowingWaterLine, RoleFlowPipeline>> CACHE_SECURITY_PIPELINE = new HashMap<>();

    private RoleModeEnum roleMode;


    private Map<String, RoleProvider> roleProviderMap;

    public AbstractValveProduceFactory(RoleModeEnum roleMode,Map<String, RoleProvider> roleProviderMap,
                               Map<String,FlowingWaterLineFactory> FLOWING_WATER_LINE_FACTORY_MAP){
        this.roleProviderMap = roleProviderMap;
        this.roleMode = roleMode;
        this.FLOWING_WATER_LINE_FACTORY_MAP.putAll(FLOWING_WATER_LINE_FACTORY_MAP);
    }

    public static void putValve(Valve valve){
        List<Valve> valves;
        ConcurrentHashMap<ValveRole, List<Valve>> valveRoleListConcurrentHashMap = CACHE_ORG_FLOWING_WATER_ROLE_VALVE_MAP.get(valve.org());
        if(CollectionUtils.isEmpty(valveRoleListConcurrentHashMap)){
            synchronized (CACHE_ORG_FLOWING_WATER_ROLE_VALVE_MAP){
                valveRoleListConcurrentHashMap = CACHE_ORG_FLOWING_WATER_ROLE_VALVE_MAP.get(valve.org());
                if(CollectionUtils.isEmpty(valveRoleListConcurrentHashMap)){
                    valveRoleListConcurrentHashMap = CACHE_ORG_FLOWING_WATER_ROLE_VALVE_MAP.get(valve.org());
                    if(CollectionUtils.isEmpty(valveRoleListConcurrentHashMap)){
                        valveRoleListConcurrentHashMap = new ConcurrentHashMap<>();
                        valves = new ArrayList<>();
                        valveRoleListConcurrentHashMap.put(valve.getValveRole(),valves);
                        CACHE_ORG_FLOWING_WATER_ROLE_VALVE_MAP.put(valve.org(),valveRoleListConcurrentHashMap);
                    }
                }
            }
        }
        valves = valveRoleListConcurrentHashMap.get(valve.getValveRole());
        if(CollectionUtils.isEmpty(valves)){
            synchronized (valveRoleListConcurrentHashMap){
                valves = valveRoleListConcurrentHashMap.get(valve.getValveRole());
                if(CollectionUtils.isEmpty(valves)){
                    valves = new ArrayList<>();
                    valveRoleListConcurrentHashMap.put(valve.getValveRole(),valves);
                }
            }
        }
        valves.add(valve);
    }

    public static <S extends RoleFlowPipeline> S getPipeline(String org, FlowingWaterLine flowingWaterLine, ValveProduceFactory valveProduceFactory){
        S produce;
        Map<FlowingWaterLine, RoleFlowPipeline> flowingWaterLineSecurityPipelineMap = CACHE_SECURITY_PIPELINE.get(org);
        if(CollectionUtils.isEmpty(flowingWaterLineSecurityPipelineMap)){
            synchronized (CACHE_SECURITY_PIPELINE){
                flowingWaterLineSecurityPipelineMap = CACHE_SECURITY_PIPELINE.get(org);
                if(CollectionUtils.isEmpty(flowingWaterLineSecurityPipelineMap)){
                    flowingWaterLineSecurityPipelineMap = new HashMap<>();
                    CACHE_SECURITY_PIPELINE.put(org,flowingWaterLineSecurityPipelineMap);
                    FlowingWaterLineFactory factory = FLOWING_WATER_LINE_FACTORY_MAP.get(org);
                    produce = (S) factory.produce(flowingWaterLine,valveProduceFactory);
                    flowingWaterLineSecurityPipelineMap.put(flowingWaterLine,produce);
                } else {
                    produce = (S) flowingWaterLineSecurityPipelineMap.get(flowingWaterLine);
                }
            }
        } else {
            produce = (S) flowingWaterLineSecurityPipelineMap.get(flowingWaterLine);
        }
        if(produce == null){
            synchronized (FLOWING_WATER_LINE_FACTORY_MAP){
                produce = (S) flowingWaterLineSecurityPipelineMap.get(flowingWaterLine);
                if(produce == null){
                    FlowingWaterLineFactory factory = FLOWING_WATER_LINE_FACTORY_MAP.get(org);
                    produce = (S) factory.produce(flowingWaterLine,valveProduceFactory);
                    flowingWaterLineSecurityPipelineMap.put(flowingWaterLine,produce);
                }
            }
        }
        return produce;

    }


    @Override
    public List<Valve> getStandardValve(FlowingWaterLine flowingWaterLine, String org){
        ConcurrentHashMap<FlowingWaterLine, List<Valve>> flowingWaterListConcurrentHashMap = CACHE_ORG_FLOWING_WATER_VALVE_MAP.get(org);
        List<Valve> valves = Collections.EMPTY_LIST;
        if(CollectionUtils.isEmpty(flowingWaterListConcurrentHashMap)){
            synchronized (CACHE_ORG_FLOWING_WATER_VALVE_MAP){
                flowingWaterListConcurrentHashMap = CACHE_ORG_FLOWING_WATER_VALVE_MAP.get(org);
                if(CollectionUtils.isEmpty(flowingWaterListConcurrentHashMap)){
                    flowingWaterListConcurrentHashMap = new ConcurrentHashMap<>();
                    ConcurrentHashMap<ValveRole, List<Valve>> valveRoleListConcurrentHashMap = CACHE_ORG_FLOWING_WATER_ROLE_VALVE_MAP.get(org);
                    if(RoleModeEnum.STRONG == roleMode){
                        //强制使用规定好的执行流程
                        RoleProvider roleProvider = roleProviderMap.get(org);
                        for (ValveRole valveRole : roleProvider.getValveRole()) {
                            List<Valve> valveList = valveRoleListConcurrentHashMap.get(valveRole);
                            if(!CollectionUtils.isEmpty(valveList)){
                                //找出对应流水线上的角色
                                List<Valve> collect = valveList.stream().filter(valve -> valve.match(flowingWaterLine))
                                        .sorted(Comparator.comparing(Valve::order)).collect(Collectors.toList());
                                Optional<List<Valve>> list = Optional.of(collect);
                                if(list.isPresent()){
                                    if(CollectionUtils.isEmpty(valves)){
                                        valves = list.get();
                                    } else {
                                        valves.addAll(list.get());
                                    }
                                }
                            }
                        }
                        flowingWaterListConcurrentHashMap.put(flowingWaterLine,valves);
                        CACHE_ORG_FLOWING_WATER_VALVE_MAP.put(org,flowingWaterListConcurrentHashMap);
                    } else {
                        //自定义执行流程
                        ArrayList<ValveRole> arrayList = new ArrayList(valveRoleListConcurrentHashMap.keySet());
                        Collections.sort(arrayList, Comparator.comparing(ValveRole::level));
                        for (ValveRole valveRole : arrayList) {
                            List<Valve> roleValueList = valveRoleListConcurrentHashMap.get(valveRole);
                            if(!CollectionUtils.isEmpty(roleValueList)){
                                List<Valve> collect = roleValueList.stream().filter(valve -> valve.match(flowingWaterLine)).sorted(Comparator.comparing(Valve::order)).collect(Collectors.toList());
                                Optional<List<Valve>> list = Optional.of(collect);
                                if(list.isPresent()){
                                    if(CollectionUtils.isEmpty(valves)){
                                        valves = list.get();
                                    } else {
                                        valves.addAll(list.get());
                                    }
                                }
                            }
                        }
                    }
                    flowingWaterListConcurrentHashMap.put(flowingWaterLine,valves);
                    CACHE_ORG_FLOWING_WATER_VALVE_MAP.put(org,flowingWaterListConcurrentHashMap);
                } else {
                    valves = flowingWaterListConcurrentHashMap.get(flowingWaterLine);
                }
            }
        } else {
            valves = flowingWaterListConcurrentHashMap.get(flowingWaterLine);
        }
        if(CollectionUtils.isEmpty(valves)){
            synchronized (CACHE_ORG_FLOWING_WATER_VALVE_MAP){
                valves = flowingWaterListConcurrentHashMap.get(flowingWaterLine);
                if(CollectionUtils.isEmpty(valves)){
                    ConcurrentHashMap<ValveRole, List<Valve>> valveRoleListConcurrentHashMap = CACHE_ORG_FLOWING_WATER_ROLE_VALVE_MAP.get(org);
                    if(RoleModeEnum.STRONG == roleMode){
                        //强制使用规定好的执行流程
                        RoleProvider roleProvider = roleProviderMap.get(org);
                        for (ValveRole valveRole : roleProvider.getValveRole()) {
                            List<Valve> valveList = valveRoleListConcurrentHashMap.get(valveRole);
                            if(!CollectionUtils.isEmpty(valveList)){
                                //找出对应流水线上的角色
                                List<Valve> collect = valveList.stream().filter(valve -> valve.match(flowingWaterLine))
                                        .sorted(Comparator.comparing(Valve::order)).collect(Collectors.toList());
                                Optional<List<Valve>> list = Optional.of(collect);
                                if(list.isPresent()){
                                    if(CollectionUtils.isEmpty(valves)){
                                        valves = list.get();
                                    } else {
                                        valves.addAll(list.get());
                                    }
                                }
                            }
                        }
                        flowingWaterListConcurrentHashMap.put(flowingWaterLine,valves);
                        CACHE_ORG_FLOWING_WATER_VALVE_MAP.put(org,flowingWaterListConcurrentHashMap);
                    } else {
                        //自定义执行流程
                        ArrayList<ValveRole> arrayList = new ArrayList(valveRoleListConcurrentHashMap.keySet());
                        Collections.sort(arrayList, Comparator.comparing(ValveRole::level));
                        for (ValveRole valveRole : arrayList) {
                            List<Valve> roleValueList = valveRoleListConcurrentHashMap.get(valveRole);
                            if(!CollectionUtils.isEmpty(roleValueList)){
                                List<Valve> collect = roleValueList.stream().filter(valve -> valve.match(flowingWaterLine)).sorted(Comparator.comparing(Valve::order)).collect(Collectors.toList());
                                Optional<List<Valve>> list = Optional.of(collect);
                                if(list.isPresent()){
                                    if(CollectionUtils.isEmpty(valves)){
                                        valves = list.get();
                                    } else {
                                        valves.addAll(list.get());
                                    }
                                }
                            }
                        }
                    }
                    flowingWaterListConcurrentHashMap.put(flowingWaterLine,valves);
                    CACHE_ORG_FLOWING_WATER_VALVE_MAP.put(org,flowingWaterListConcurrentHashMap);
                }
            }
        }
        return valves;
    }
}
