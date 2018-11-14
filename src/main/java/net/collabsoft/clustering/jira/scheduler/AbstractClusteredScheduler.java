
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

    public JobId getJobId() {
        return JobId.of(getJobName());
    }

    public JobId getJobId(String suffix) {
        return JobId.of(getJobName(suffix));
    }
    
    public String getJobName() {
        String className = getPluginJob().getClass().getName();
        return className + ":job";
    }

    public String getJobName(String suffix) {
        String className = getPluginJob().getClass().getName();
        return className + ":job:" + suffix;
    }
    
    public Date getFirstRunDate() {
        final int jitter = RANDOM.nextInt(MAX_JITTER);
        return new Date(System.currentTimeMillis() + MIN_DELAY + jitter);
    }

    public abstract Map<String, Object> getJobData();
    public abstract AbstractClusteredTask getPluginJob();
    public abstract String getPluginKey();
    
    // ----------------------------------------------------------------------------------------------- Public methods

    public void unschedulePreviouslyScheduledJob() {
        try {
            schedulerService.unscheduleJob(getJobId());
            schedulerService.unregisterJobRunner(JobRunnerKey.of(getJobId().toString()));
        } catch (Exception e) {
            //don't worry about this exception. just means that the job hadn't yet been added to the scheduler.
        }        
    }
    
    public void initializeJobRunner(final ClusteredTask job, final Map<String, Object> data) {
        this.schedulerService.registerJobRunner(JobRunnerKey.of(getJobId().toString()), new JobRunner() {
            public JobRunnerResponse runJob(JobRunnerRequest request) {
                return job.execute(data);
            }
        });
    }
    
    // use schedule(ClusteredTask task, Long interval);
    // in the implementation of either of the schedule abstract methods to schedule a task;
    public abstract void schedule();

    public void schedule(Long interval) {
        throw new UnsupportedOperationException("This operation is currently not implemented for this class");
    }

    public void schedule(String cronExpression) throws SchedulerServiceException {
        this.schedule(getPluginJob(), Schedule.forCronExpression(cronExpression));
    }

    public void schedule(Schedule schedule) throws SchedulerServiceException {
        this.schedule(getPluginJob(), schedule);
    }

    public void schedule(ClusteredTask task, Long interval) throws SchedulerServiceException {
        this.schedule(getJobName(), task, Schedule.forInterval(TimeUnit.SECONDS.toMillis(interval), getFirstRunDate()));
    }

    public void schedule(ClusteredTask task, Schedule schedule) throws SchedulerServiceException {
        this.schedule(getJobName(), task, getJobData(), schedule);
    }
    
    public void schedule(ClusteredTask task, Map<String, Object> jobData, Long interval) throws SchedulerServiceException {
        this.schedule(getJobName(), task, jobData, Schedule.forInterval(TimeUnit.SECONDS.toMillis(interval), getFirstRunDate()));
    }
    
    public void schedule(ClusteredTask task, Map<String, Object> jobData, Schedule schedule) throws SchedulerServiceException {
        this.schedule(getJobName(), task, jobData, schedule);
   }

    public void schedule(String name, ClusteredTask task, Long interval) throws SchedulerServiceException {
        this.schedule(JobId.of(name), task, Schedule.forInterval(TimeUnit.SECONDS.toMillis(interval), getFirstRunDate()));
    }

    public void schedule(String name, ClusteredTask task, Schedule schedule) throws SchedulerServiceException {
        this.schedule(JobId.of(name), task, getJobData(), schedule);
    }

    public void schedule(String name, ClusteredTask task, Map<String, Object> jobData, Long interval) throws SchedulerServiceException {
        this.schedule(JobId.of(name), task, jobData, Schedule.forInterval(TimeUnit.SECONDS.toMillis(interval), getFirstRunDate()));
    }

    public void schedule(String name, ClusteredTask task, Map<String, Object> jobData, Schedule schedule) throws SchedulerServiceException {
        this.schedule(JobId.of(name), task, jobData, schedule);
    }

    public void schedule(JobId jobId, ClusteredTask task, Long interval) throws SchedulerServiceException {
        this.schedule(jobId, task, Schedule.forInterval(TimeUnit.SECONDS.toMillis(interval), getFirstRunDate()));
    }

    public void schedule(JobId jobId, ClusteredTask task, Schedule schedule) throws SchedulerServiceException {
        this.schedule(jobId, task, getJobData(), schedule);
    }

    public void schedule(JobId jobId, ClusteredTask task, Map<String, Object> jobData, Long interval) throws SchedulerServiceException {
        this.schedule(jobId, task, jobData, Schedule.forInterval(TimeUnit.SECONDS.toMillis(interval), getFirstRunDate()));
    }

    public void schedule(JobId jobId, ClusteredTask task, Map<String, Object> jobData, Schedule schedule) throws SchedulerServiceException {
        initializeJobRunner(task, jobData);
        schedulerService.scheduleJob(jobId, getJobConfig(schedule));
    }

    public void scheduleOnce(ClusteredTask task) throws SchedulerServiceException {
        this.scheduleOnce(task, getJobData());
    }
    
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
