package com.infrastructure.role.flow.intface;

/**
 * 每个valve对应的角色,规定操作流程
 * @author zzh
 * @description
 * @date 2023-06-29 11:50
 */
public interface RoleProvider extends Org{



    ValveRole[] getValveRole();

}
