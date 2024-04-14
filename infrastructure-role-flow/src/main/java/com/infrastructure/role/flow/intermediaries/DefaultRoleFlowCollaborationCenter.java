package com.infrastructure.role.flow.intermediaries;

/**
 *
 */
public class DefaultRoleFlowCollaborationCenter extends AbstractRoleFlowCollaborationCenter {


    /**
     *
     * @param source
     * @param target
     */
    @Override
    public <S, T> void coordination(S source, T target) {
        Intermediaries intermediaries = matchMediator(source.getClass(), target.getClass());
        if(intermediaries != null){
            intermediaries.coordination(source,target);
        }
    }
}
