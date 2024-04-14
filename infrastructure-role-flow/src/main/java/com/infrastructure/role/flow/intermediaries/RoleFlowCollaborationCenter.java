package com.infrastructure.role.flow.intermediaries;

/**
 */
public interface RoleFlowCollaborationCenter {


    /**
     *
     * @param source
     * @param target
     * @param <S> 源
     * @param <T> 目标
     */
    <S,T> void coordination(S source, T target);


    /**
     *
     * @param intermediaries
     */
    void addMediator(Intermediaries intermediaries);

    void clear();
}
