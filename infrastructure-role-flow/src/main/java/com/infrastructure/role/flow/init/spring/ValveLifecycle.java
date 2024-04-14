package com.infrastructure.role.flow.init.spring;

import com.infrastructure.role.flow.factory.DefaultValveFactory;
import com.infrastructure.role.flow.valves.Valve;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ValveLifecycle implements SmartLifecycle, ApplicationContextAware {


    private Environment environment;


    private ApplicationContext applicationContext;

    private volatile boolean start = false;


    /**
     * 环境
     * Start this component.
     * <p>Should not throw an exception if the component is already running.
     * <p>In the case of a container, this will propagate the start signal to all
     * components that apply.
     *
     * @see SmartLifecycle#isAutoStartup()
     */
    @Override
    public void start() {
        start = true;
        Map<String, Valve> beansOfType = applicationContext.getBeansOfType(Valve.class);
        for (Map.Entry<String, Valve> stringValveEntry : beansOfType.entrySet()) {
            Valve value = stringValveEntry.getValue();
            DefaultValveFactory.putValve(value);
        }

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

    /**
     * Set the {@code Environment} that this component runs in.
     *
     * @param environment
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }




    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
