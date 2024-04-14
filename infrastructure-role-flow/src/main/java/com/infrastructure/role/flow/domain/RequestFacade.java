package com.infrastructure.role.flow.domain;

import com.infrastructure.role.flow.intermediaries.RoleFlowCollaborationCenter;

/**
 * 请求的包装类，内部提供参数填充
 * @param <T> 具体的请求对象
 */
public interface RequestFacade<T> extends ContextFacade {

    /**
     * 获取发送请求报文
     * @return
     */
    T getRequestData();


    /**
     * 协调参数，通过 @param intermediariesStore 将原请求实体与处理业务逻辑过程中生产的参数对象进行协调，比如参数互换等
     * @param roleFlowCollaborationCenter
     * @Date 2024/4/14 13:58
    **/
    void coordinationInternalDto(RoleFlowCollaborationCenter roleFlowCollaborationCenter);

    /**
     *  协调参数，通过 @param intermediariesStore 将原请求实体与otherObj进行协调，比如参数互换等
     * @param otherObj
     * @param roleFlowCollaborationCenter
     * @Date 2024/4/14 14:06
    **/
    <O>void coordinationRpcDto(O otherObj, RoleFlowCollaborationCenter roleFlowCollaborationCenter);





}
