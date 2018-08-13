
package net.collabsoft.clustering.jira.scheduler;

import java.util.Map;
import org.apache.log4j.Logger;

public interface Scheduler {

    public String getJobName();
    public String getJobName(String suffix);
    public abstract Map<String, Object> getJobData();
    public abstract Object getPluginJob();
    public abstract String getPluginKey();
    public abstract Logger getLogger();
    
    public void unschedulePreviouslyScheduledJob();

    public void schedule();
    public void schedule(Long interval);
    
}
