package com.infrastructure.role.flow.valves;

import com.infrastructure.role.flow.domain.RequestFacade;
import com.infrastructure.role.flow.domain.ResponseFacade;
import com.infrastructure.role.flow.intface.FlowingWaterLine;
import com.infrastructure.role.flow.intface.Org;
import com.infrastructure.role.flow.intface.ValveRole;

/**
 * 阀
 */
public interface Valve<Request, Response> extends Org {


    /**
     * Useful constant for the highest precedence value.
     * @see Integer#MIN_VALUE
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * Useful constant for the lowest precedence value.
     * @see Integer#MAX_VALUE
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    void invoke(RequestFacade<Request> request, ResponseFacade<Response> response, ValveSensor valveSensor);

    ValveRole getValveRole();

    boolean match(FlowingWaterLine duty);

    int order();
}
