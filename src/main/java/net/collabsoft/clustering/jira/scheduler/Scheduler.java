
package net.collabsoft.clustering.jira.scheduler;

import java.util.Map;
import org.apache.log4j.Logger;

public interface Scheduler {

    Long getInterval();
    String getJobName();
    String getJobName(String suffix);
    Map<String, Object> getJobData();
    Object getPluginJob();
    String getPluginKey();
    Logger getLogger();
    
    void unschedulePreviouslyScheduledJob();

    void schedule();
    void schedule(Long interval);
    
}
