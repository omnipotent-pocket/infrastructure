package com.infrastructure.role.flow.domain;


import com.infrastructure.role.flow.intermediaries.RoleFlowCollaborationCenter;

/**
 *
 * @param <T>
 */
public abstract class AbstractRequestFacade<T> implements RequestFacade<T> {


    protected RequestFacade<T> expand;


    /**
     * 协调参数，通过 @param intermediariesStore 将原请求实体与处理业务逻辑过程中生产的参数对象进行协调，比如参数互换等
     *
     * @param roleFlowCollaborationCenter
     * @Date 2024/4/14 13:58
     **/
    @Override
    public void coordinationInternalDto(RoleFlowCollaborationCenter roleFlowCollaborationCenter) {
        if(expand != null){
            expand.coordinationInternalDto(roleFlowCollaborationCenter);
        }
        roleFlowCollaborationCenter.coordination(this,this.getRequestData());
    }

    /**
     * 协调参数，通过 @param intermediariesStore 将原请求实体与otherObj进行协调，比如参数互换等
     *
     * @param otherObj
     * @param roleFlowCollaborationCenter
     * @Date 2024/4/14 14:06
     **/
    @Override
    public <O> void coordinationRpcDto(O otherObj, RoleFlowCollaborationCenter roleFlowCollaborationCenter) {
        if(expand != null){
            expand.coordinationRpcDto(otherObj, roleFlowCollaborationCenter);
        }
        roleFlowCollaborationCenter.coordination(this,otherObj);
    }


}
