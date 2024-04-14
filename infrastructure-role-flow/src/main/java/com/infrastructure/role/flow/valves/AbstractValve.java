package com.infrastructure.role.flow.valves;

import com.infrastructure.role.flow.domain.RequestFacade;
import com.infrastructure.role.flow.domain.ResponseFacade;
import com.infrastructure.role.flow.intermediaries.RoleFlowCollaborationCenter;
import com.infrastructure.role.flow.intermediaries.CollaborationCenterFactory;


public abstract class AbstractValve<Request, Response> implements Valve<Request, Response> {



    protected RoleFlowCollaborationCenter defaultRoleFlowCollaborationCenter;


    public AbstractValve(){
        this.defaultRoleFlowCollaborationCenter = CollaborationCenterFactory.getDefaultRoleFlowCollaborationCenter();
    }
    /**
     * @Description   
     * @param request
     * @param response
     * @param valveSensor
     * @Date 2024/4/14 13:46
    **/
    @Override
    public void invoke(RequestFacade<Request> request, ResponseFacade<Response> response, ValveSensor valveSensor) {
        valveSensor.invokeNext(internalInvoke(request,response),response);
    }


    protected abstract RequestFacade<Request> internalInvoke(RequestFacade<Request> request, ResponseFacade<Response> response);

    @Override
    public int order() {
        return HIGHEST_PRECEDENCE;
    }
}
