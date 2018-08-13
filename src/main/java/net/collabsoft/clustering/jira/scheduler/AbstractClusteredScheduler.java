
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
        String className = getPluginJob().getClass().getName();
        return className + ":job";
    }

    @Override 
    public String getJobName(String suffix) {
        String className = getPluginJob().getClass().getName();
        return className + ":job:" + suffix;
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
    // in the implementation of either of the schedule abstract methods to schedule a task;
    @Override public abstract void schedule();

    @Override 
    public void schedule(Long interval) {
        throw new UnsupportedOperationException("This operation is currently not implemented for this class");
    }

    @Override 
    public void schedule(String cronExpression) throws SchedulerServiceException {
        this.schedule(getPluginJob(), Schedule.forCronExpression(cronExpression));
    }

    @Override public void schedule(Schedule schedule) throws SchedulerServiceException {
        this.schedule(getPluginJob(), schedule);
    }

    @Override
    public void schedule(ClusteredTask task, Long interval) throws SchedulerServiceException {
        this.schedule(task, Schedule.forInterval(TimeUnit.SECONDS.toMillis(interval), getFirstRunDate()));
    }

    @Override
    public void schedule(ClusteredTask task, Schedule schedule) throws SchedulerServiceException {
        this.schedule(task, getJobData(), schedule);
    }
    
    @Override
    public void schedule(ClusteredTask task, Map<String, Object> jobData, Long interval) throws SchedulerServiceException {
        this.schedule(task, jobData, Schedule.forInterval(TimeUnit.SECONDS.toMillis(interval), getFirstRunDate()));
    }
    
    @Override
    public void schedule(ClusteredTask task, Map<String, Object> jobData, Schedule schedule) throws SchedulerServiceException {
        initializeJobRunner(task, jobData);
        schedulerService.scheduleJob(getJobId(), getJobConfig(schedule));
    }
    
    @Override 
    public void scheduleOnce(ClusteredTask task) throws SchedulerServiceException {
        this.scheduleOnce(task, getJobData());
    }
    
    @Override
    public void scheduleOnce(ClusteredTask task, Map<String, Object> jobData) throws SchedulerServiceException {
        initializeJobRunner(task, jobData);
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
