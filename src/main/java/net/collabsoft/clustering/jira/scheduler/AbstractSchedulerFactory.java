
package net.collabsoft.clustering.jira.scheduler;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.extension.JiraStartedEvent;
import com.atlassian.jira.util.BuildUtilsInfoImpl;
import com.atlassian.jira.util.system.VersionNumber;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.scheduler.SchedulerService;

public abstract class AbstractSchedulerFactory implements SchedulerFactory {
    
    private boolean initialized = false;
    private final EventPublisher eventPublisher;
    private final int JIRA_MIN_MAJOR_VERSION = 6;
    private final int JIRA_MIN_MINOR_VERSION = 3;
    
    // ----------------------------------------------------------------------------------------------- Constructor

    public AbstractSchedulerFactory(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

    public abstract String getPluginKey();

    public Scheduler getScheduler() {
        if(getSchedulerService() == null) {
            return getLocalScheduler();
        } else {
            return getClusteredScheduler();
        }
    }
    public abstract Scheduler getLocalScheduler();
    public abstract Scheduler getClusteredScheduler();
    public Object getSchedulerService() {
        try {
            String version = new BuildUtilsInfoImpl().getVersion();
            VersionNumber versionNumber = new VersionNumber(version);
            if(versionNumber.isGreaterThanOrEquals(new VersionNumber(JIRA_MIN_MAJOR_VERSION + "." + JIRA_MIN_MINOR_VERSION))) {
                return ComponentAccessor.getComponent(SchedulerService.class);
            } else {
                return null;
            }
        } catch(RuntimeException ex) {
            return null;
        } catch(NoClassDefFoundError ex) {
            return null;
        }
    }

    // ----------------------------------------------------------------------------------------------- Event Handlers
    
    @EventListener
    public void onJiraStarted(final JiraStartedEvent event) {
        initialized = true;
        onStart();
    }
    
    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (getPluginKey().equals(event.getPlugin().getKey()) && initialized) {
            onStart();
        }
    }

    @EventListener
    public void onPluginDisableEvent(PluginDisabledEvent event) {
        if (getPluginKey().equals(event.getPlugin().getKey())) {
            Scheduler scheduler = getScheduler();
            scheduler.unschedulePreviouslyScheduledJob();
        }
    }

    public void afterPropertiesSet() {
        eventPublisher.register(this);
    }
    
    public abstract void onStart();    

    public void destroy() {
        eventPublisher.unregister(this);
        getScheduler().unschedulePreviouslyScheduledJob();
    }
    
    // ----------------------------------------------------------------------------------------------- Public methods

    // ----------------------------------------------------------------------------------------------- Private methods

    // ----------------------------------------------------------------------------------------------- Private Getters & Setters
    
}
