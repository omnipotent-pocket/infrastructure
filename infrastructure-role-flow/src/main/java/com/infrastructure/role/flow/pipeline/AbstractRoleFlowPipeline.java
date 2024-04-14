package com.infrastructure.role.flow.pipeline;

import com.infrastructure.role.flow.domain.RequestFacade;
import com.infrastructure.role.flow.domain.ResponseFacade;
import com.infrastructure.role.flow.engine.Engine;
import com.infrastructure.role.flow.engine.MajorControlRoom;
import com.infrastructure.role.flow.factory.ValveProduceFactory;
import com.infrastructure.role.flow.init.RoleFlowLifecycle;


/**
 *
 * @param <Request>
 * @param <Response>
 */
public abstract class AbstractRoleFlowPipeline<Request, Response> implements RoleFlowPipeline<Request, Response>, RoleFlowLifecycle {


    protected Engine engine = new MajorControlRoom();

    protected ValveProduceFactory valveProduceFactory;

    public AbstractRoleFlowPipeline(ValveProduceFactory valveProduceFactory){
        this.valveProduceFactory = valveProduceFactory;
        start();
    }


    @Override
    public Response action(Request requestData) {
        ResponseFacade<Response> responseFacade = getResponseFacade();
        engine.authorize(getRequestFacade(requestData),responseFacade);
        return responseFacade.getResultData();
    }


    protected abstract RequestFacade<Request> getRequestFacade(Request reqeusetData);

    protected abstract ResponseFacade<Response> getResponseFacade();

    @Override
    public void start() {
        engine.addValveList(valveProduceFactory.getStandardValve(operateBusiness(),org()));
    }
}
