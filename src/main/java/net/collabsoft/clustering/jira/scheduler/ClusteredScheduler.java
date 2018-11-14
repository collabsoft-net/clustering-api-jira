
package net.collabsoft.clustering.jira.scheduler;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.Schedule;
import java.util.Date;
import java.util.Map;

public interface ClusteredScheduler extends Scheduler {

    // ----------------------------------------------------------------------------------------------- Constructor

    // ----------------------------------------------------------------------------------------------- Getters & Setters

    JobId getJobId();
    JobId getJobId(String suffix);
    Date getFirstRunDate();
    
    // ----------------------------------------------------------------------------------------------- Event Handlers
    
    // ----------------------------------------------------------------------------------------------- Public methods

    void initializeJobRunner(final ClusteredTask job, final Map<String, Object> data);

    void schedule();
    void schedule(String cronExpression)  throws SchedulerServiceException;
    void schedule(Schedule schedule) throws SchedulerServiceException;

    void schedule(ClusteredTask task, Long interval) throws SchedulerServiceException;
    void schedule(ClusteredTask task, Schedule schedule) throws SchedulerServiceException;
    void schedule(ClusteredTask task, Map<String, Object> jobData, Long interval) throws SchedulerServiceException;
    void schedule(ClusteredTask task, Map<String, Object> jobData, Schedule schedule) throws SchedulerServiceException;
    void schedule(String name, ClusteredTask task, Long interval) throws SchedulerServiceException;
    void schedule(String name, ClusteredTask task, Schedule schedule) throws SchedulerServiceException;
    void schedule(String name, ClusteredTask task, Map<String, Object> jobData, Long interval) throws SchedulerServiceException;
    void schedule(String name, ClusteredTask task, Map<String, Object> jobData, Schedule schedule) throws SchedulerServiceException;
    void schedule(JobId jobId, ClusteredTask task, Long interval) throws SchedulerServiceException;
    void schedule(JobId jobId, ClusteredTask task, Schedule schedule) throws SchedulerServiceException;
    void schedule(JobId jobId, ClusteredTask task, Map<String, Object> jobData, Long interval) throws SchedulerServiceException;
    void schedule(JobId jobId, ClusteredTask task, Map<String, Object> jobData, Schedule schedule) throws SchedulerServiceException;

    void scheduleOnce(ClusteredTask task) throws SchedulerServiceException;
    void scheduleOnce(ClusteredTask task, Map<String, Object> jobData) throws SchedulerServiceException;
    
    // ----------------------------------------------------------------------------------------------- Private methods
    
    
    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

}
