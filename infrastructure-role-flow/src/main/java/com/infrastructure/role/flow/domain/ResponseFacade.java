package com.infrastructure.role.flow.domain;

import com.infrastructure.role.flow.intermediaries.RoleFlowCollaborationCenter;

public interface ResponseFacade<R> extends ContextFacade {

    /**
     *  协调参数，通过 @param intermediariesStore 将响应实体与处理业务逻辑过程中生产的参数对象进行协调，比如参数互换等
     * @param roleFlowCollaborationCenter
     * @Date 2024/4/14 14:07
    **/
    void coordinationInternalDto(RoleFlowCollaborationCenter roleFlowCollaborationCenter);

    /**
     *  协调参数，通过 @param intermediariesStore 将原请求实体与otherObj进行协调，比如参数互换等
     * @param otherObj
     * @param roleFlowCollaborationCenter
     * @Date 2024/4/14 14:09
    **/
    <H>void coordinationRpcDto(H otherObj, RoleFlowCollaborationCenter roleFlowCollaborationCenter);

    public void setResultData(R resultData);
    public R getResultData();


}
