package com.infrastructure.role.flow.valves;

import com.infrastructure.role.flow.domain.RequestFacade;
import com.infrastructure.role.flow.domain.ResponseFacade;

/**
 * Valve感应器，用于进入下一个Valve
 */
public interface ValveSensor {


    public void invokeNext(RequestFacade request, ResponseFacade response);

}
