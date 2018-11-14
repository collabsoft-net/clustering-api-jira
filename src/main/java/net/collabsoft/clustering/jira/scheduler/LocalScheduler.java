
package net.collabsoft.clustering.jira.scheduler;

import java.util.Map;

public interface LocalScheduler extends Scheduler {

    // ----------------------------------------------------------------------------------------------- Constructor

    // ----------------------------------------------------------------------------------------------- Getters & Setters
    
    // ----------------------------------------------------------------------------------------------- Event Handlers
    
    // ----------------------------------------------------------------------------------------------- Public methods

    // ----------------------------------------------------------------------------------------------- Private methods

    Runnable getRunnableJob(final AbstractLocalTask job, final Map<String, Object> data);
    void schedule(Runnable job, String jobName, Long interval);
    void scheduleOnce(final Runnable job, String jobName, final long delay);
}
