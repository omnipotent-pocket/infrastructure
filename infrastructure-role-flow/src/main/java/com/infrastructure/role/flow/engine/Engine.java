package com.infrastructure.role.flow.engine;

import com.infrastructure.role.flow.domain.RequestFacade;
import com.infrastructure.role.flow.domain.ResponseFacade;
import com.infrastructure.role.flow.valves.Valve;

import java.util.List;

public interface Engine {

    void authorize(RequestFacade request, ResponseFacade response);

    void addValve(Valve valve);

    void addValveList(List<Valve> valveList);

    List<Valve> getValveList();
}
