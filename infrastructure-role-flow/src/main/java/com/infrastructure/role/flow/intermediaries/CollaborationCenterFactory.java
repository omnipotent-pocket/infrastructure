package com.infrastructure.role.flow.intermediaries;

/**
 * 协作中心工厂
 */
public class CollaborationCenterFactory {


    private static final DefaultRoleFlowCollaborationCenter DEFAULT_COLLABORATION_CENTER = new DefaultRoleFlowCollaborationCenter();


    /**
     * @param intermediaries
     */
    public static void addMediator(Intermediaries intermediaries){
        DEFAULT_COLLABORATION_CENTER.addMediator(intermediaries);
    }

    public static RoleFlowCollaborationCenter getDefaultRoleFlowCollaborationCenter(){
        return DEFAULT_COLLABORATION_CENTER;
    }


}
