package com.infrastructure.role.flow.domain;


import com.infrastructure.role.flow.intermediaries.RoleFlowCollaborationCenter;

/**
 *
 * @param <R>
 */
public abstract class AbstractResponseFacade<R> implements  ResponseFacade<R>{


    protected ResponseFacade<R> expand;

    protected R resultData;


    @Override
    public void coordinationInternalDto(RoleFlowCollaborationCenter roleFlowCollaborationCenter) {
        if(expand != null){
            expand.coordinationInternalDto(roleFlowCollaborationCenter);
        }
        roleFlowCollaborationCenter.coordination(this,resultData);
    }

    @Override
    public <H> void coordinationRpcDto(H otherObj, RoleFlowCollaborationCenter roleFlowCollaborationCenter) {
        if(expand != null){
            expand.coordinationRpcDto(otherObj, roleFlowCollaborationCenter);
        }
        roleFlowCollaborationCenter.coordination(this,otherObj);
    }

    @Override
    public void setResultData(R resultData) {
        this.resultData = resultData;
    }
}
