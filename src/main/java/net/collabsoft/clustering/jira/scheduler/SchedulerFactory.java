
package net.collabsoft.clustering.jira.scheduler;

import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface SchedulerFactory extends LifecycleAware, InitializingBean, DisposableBean {

    Scheduler getScheduler();
    Scheduler getLocalScheduler();
    Scheduler getClusteredScheduler();
    Object getSchedulerService();
    String getPluginKey();

    void onPluginEnabled(PluginEnabledEvent event);
    void onPluginDisableEvent(PluginDisabledEvent event);
}
