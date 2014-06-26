
package net.collabsoft.clustering.jira.scheduler;

import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface SchedulerFactory extends LifecycleAware, InitializingBean, DisposableBean {

    public Scheduler getScheduler();
    public Scheduler getLocalScheduler();
    public Scheduler getClusteredScheduler();
    public Object getSchedulerService();
    public String getPluginKey();
    
    @EventListener public void onPluginEnabled(PluginEnabledEvent event);
    @EventListener public void onPluginDisableEvent(PluginDisabledEvent event);
}
