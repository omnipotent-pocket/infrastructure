package com.infrastructure.role.flow.factory;

import com.infrastructure.role.flow.intface.FlowingWaterLine;
import com.infrastructure.role.flow.valves.Valve;

import java.util.List;

/**
 */
public interface ValveProduceFactory {

    List<Valve> getStandardValve(FlowingWaterLine duty, String org);
}
