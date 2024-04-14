package com.infrastructure.role.flow.init.spring;

import com.infrastructure.role.flow.intermediaries.CollaborationCenterFactory;
import com.infrastructure.role.flow.intermediaries.Intermediaries;
import com.infrastructure.role.flow.intermediaries.RoleFlowCollaborationCenter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 智能
 * @author jie.jiang
 * @version 1.0
 * @description: 实体转换器注册类
 * @date 2021/9/3 2:29 下午
 */
public class MediatorAutoRegister {

    @Configuration
    static class MediatorStoreConfig {

        @Bean
        @ConditionalOnMissingBean(RoleFlowCollaborationCenter.class)
        public RoleFlowCollaborationCenter defaultMediatorStore() {
            return CollaborationCenterFactory.getDefaultRoleFlowCollaborationCenter();
        }
    }


    @Component
    public static class MediatorLifecycle implements SmartLifecycle{

        @Autowired(required = false)
        private Map<String, Intermediaries> collaborationMap;

        @Autowired
        private RoleFlowCollaborationCenter defaultRoleFlowCollaborationCenter;

        private volatile boolean start = false;

        /**
         * Start this component.
         * <p>Should not throw an exception if the component is already running.
         * <p>In the case of a container, this will propagate the start signal to all
         * components that apply.
         *
         * @see SmartLifecycle#isAutoStartup()
         */
        @Override
        public void start() {
            if (collaborationMap != null) {
                collaborationMap
                        .values()
                        .stream()
                        .forEach(defaultRoleFlowCollaborationCenter::addMediator);
            }
            start = true;
        }

        /**
         * Stop this component, typically in a synchronous fashion, such that the component is
         * fully stopped upon return of this method. Consider implementing {@link SmartLifecycle}
         * and its {@code stop(Runnable)} variant when asynchronous stop behavior is necessary.
         * <p>Note that this stop notification is not guaranteed to come before destruction:
         * On regular shutdown, {@code Lifecycle} beans will first receive a stop notification
         * before the general destruction callbacks are being propagated; however, on hot
         * refresh during a context's lifetime or on aborted refresh attempts, a given bean's
         * destroy method will be called without any consideration of stop signals upfront.
         * <p>Should not throw an exception if the component is not running (not started yet).
         * <p>In the case of a container, this will propagate the stop signal to all components
         * that apply.
         *
         * @see SmartLifecycle#stop(Runnable)
         * @see DisposableBean#destroy()
         */
        @Override
        public void stop() {
            if(!CollectionUtils.isEmpty(collaborationMap)){
                collaborationMap.clear();
            }
            defaultRoleFlowCollaborationCenter.clear();
            start = false;
        }

        /**
         * Check whether this component is currently running.
         * <p>In the case of a container, this will return {@code true} only if <i>all</i>
         * components that apply are currently running.
         *
         * @return whether the component is currently running
         */
        @Override
        public boolean isRunning() {
            return start;
        }
    }
}