
package net.collabsoft.clustering.jira.scheduler;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class AbstractClusteredScheduler implements ClusteredScheduler {

    private static final int MAX_JITTER = 10000;
    private static final int MIN_DELAY = 15000;
    private static final Random RANDOM = new Random();

    private final SchedulerService schedulerService;
    
    // ----------------------------------------------------------------------------------------------- Constructor

    public AbstractClusteredScheduler(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
    
    // ----------------------------------------------------------------------------------------------- Getters & Setters

    @Override public abstract Long getInterval();

    @Override 
    public JobId getJobId() {
        return JobId.of(getJobName());
    }

    @Override 
    public JobId getJobId(String suffix) {
        return JobId.of(getJobName(suffix));
    }
    
    @Override 
    public String getJobName() {
        return getPluginJob().getClass().getSimpleName() + ":job";
    }

    @Override 
    public String getJobName(String suffix) {
        return getPluginJob().getClass().getSimpleName() + ":job:" + suffix;
    }
    
    @Override 
    public Date getFirstRunDate() {
        final int jitter = RANDOM.nextInt(MAX_JITTER);
        return new Date(System.currentTimeMillis() + MIN_DELAY + jitter);
    }

    @Override public abstract Map<String, Object> getJobData();
    @Override public abstract AbstractClusteredTask getPluginJob();
    @Override public abstract String getPluginKey();
    
    // ----------------------------------------------------------------------------------------------- Public methods

    @Override
    public void unschedulePreviouslyScheduledJob() {
        try {
            schedulerService.unscheduleJob(getJobId());
            schedulerService.unregisterJobRunner(JobRunnerKey.of(getJobId().toString()));
        } catch (Exception e) {
            //don't worry about this exception. just means that the job hadn't yet been added to the scheduler.
        }        
    }
    
    @Override
    public void initializeJobRunner(final ClusteredTask job, final Map<String, Object> data) {
        this.schedulerService.registerJobRunner(JobRunnerKey.of(getJobId().toString()), new JobRunner() {

            @Override
            public JobRunnerResponse runJob(JobRunnerRequest request) {
                return job.execute(data);
            }
        });
    }
    
    // use schedule(ClusteredTask task, Long interval);
    // in the implementation of the schedule(Long interval) abstract method to schedule a task;
    @Override public abstract void schedule(Long interval);

    @Override
    public void schedule(ClusteredTask task, Long interval) throws SchedulerServiceException {
        initializeJobRunner(task, getJobData());
        schedulerService.scheduleJob(getJobId(), getJobConfig(Schedule.forInterval(TimeUnit.SECONDS.toMillis(interval), getFirstRunDate())));
    }
    
    @Override 
    public void scheduleOnce(ClusteredTask task, Long interval) throws SchedulerServiceException {
        initializeJobRunner(task, getJobData());
        schedulerService.scheduleJob(getJobId(), getJobConfig(Schedule.runOnce(getFirstRunDate())));
    }
    
    // ----------------------------------------------------------------------------------------------- Private methods
    
    
    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

    private JobConfig getJobConfig(Schedule schedule) {
        return JobConfig.forJobRunnerKey(JobRunnerKey.of(getJobId().toString()))
                        .withSchedule(schedule);
    }

    private JobConfig getJobConfig(RunMode runMode, Schedule schedule) {
        return JobConfig.forJobRunnerKey(JobRunnerKey.of(getJobId().toString()))
                        .withRunMode(runMode)
                        .withSchedule(schedule);
    }
    
}
