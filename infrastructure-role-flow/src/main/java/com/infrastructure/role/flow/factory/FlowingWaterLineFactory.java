package com.infrastructure.role.flow.factory;

import com.infrastructure.role.flow.intface.FlowingWaterLine;
import com.infrastructure.role.flow.intface.Org;
import com.infrastructure.role.flow.pipeline.RoleFlowPipeline;

/**
 * @author zzh
 * @description
 * @date 2023-06-30 16:57
 */
public interface FlowingWaterLineFactory extends Org {

    /**
     *
     * @return
     */
    RoleFlowPipeline produce(FlowingWaterLine flowingWaterLine, ValveProduceFactory valveProduceFactory);

}
