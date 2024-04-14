package com.infrastructure.role.flow.pipeline;

import com.infrastructure.role.flow.intface.FlowingWaterLine;
import com.infrastructure.role.flow.intface.Org;

public interface RoleFlowPipeline<Request, Response> extends Org {


    public Response action(Request requestData);

    /**
     * 操作业务
     */
    FlowingWaterLine operateBusiness();
}
