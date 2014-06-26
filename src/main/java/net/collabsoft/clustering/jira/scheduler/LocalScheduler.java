
package net.collabsoft.clustering.jira.scheduler;

import java.util.Map;

public interface LocalScheduler extends Scheduler {

    // ----------------------------------------------------------------------------------------------- Constructor

    // ----------------------------------------------------------------------------------------------- Getters & Setters
    
    // ----------------------------------------------------------------------------------------------- Event Handlers
    
    // ----------------------------------------------------------------------------------------------- Public methods

    // ----------------------------------------------------------------------------------------------- Private methods

    public Runnable getRunnableJob(final AbstractLocalTask job, final Map<String, Object> data);
    public void schedule(Runnable job, String jobName, Long interval);
    public void scheduleOnce(final Runnable job, String jobName, final long delay);
}
