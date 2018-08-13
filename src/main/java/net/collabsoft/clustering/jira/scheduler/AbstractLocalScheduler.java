
package net.collabsoft.clustering.jira.scheduler;

import com.atlassian.util.concurrent.ThreadFactories;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.log4j.Logger;

public abstract class AbstractLocalScheduler implements LocalScheduler {

    private ScheduledExecutorService schedulerThread;
    
    // ----------------------------------------------------------------------------------------------- Constructor

    // ----------------------------------------------------------------------------------------------- Getters & Setters

    @Override
    public String getJobName() {
        return getPluginJob().getClass().getSimpleName() + ":job";
    }

    @Override
    public String getJobName(String suffix) {
        return getPluginJob().getClass().getSimpleName() + ":job:" + suffix;
    }

    @Override public abstract Map<String, Object> getJobData();
    @Override public abstract AbstractLocalTask getPluginJob();
    @Override public abstract String getPluginKey();
    @Override public abstract Logger getLogger();
    
    // ----------------------------------------------------------------------------------------------- Public methods

    // use schedule(Long interval);
    // in the implementation of the schedule() abstract methods to schedule a task;
    @Override public abstract void schedule();
    
    @Override
    public void schedule(Long interval) {
        schedule(getRunnableJob(getPluginJob(), getJobData()), getJobName(), interval);
    }
    
    @Override
    public void unschedulePreviouslyScheduledJob() {
        try {
            schedulerThread.shutdown();
        } catch (Exception e) {
            //don't worry about this exception. just means that the job hadn't yet been added to the scheduler.
        }        
    }

    // ----------------------------------------------------------------------------------------------- Private methods

    @Override
    public Runnable getRunnableJob(final AbstractLocalTask job, final Map<String, Object> data) {
        return new Runnable() {
            @SuppressWarnings("serial")
            @Override
            public void run() {
                job.execute(data);
            }
        };
    }
    
    @Override
    public void schedule(Runnable job, String jobName, Long interval) {
        schedulerThread = Executors.newSingleThreadScheduledExecutor(
            ThreadFactories.namedThreadFactory(jobName, com.atlassian.util.concurrent.ThreadFactories.Type.DAEMON)
        );
        schedulerThread.scheduleAtFixedRate(job, 0, interval, TimeUnit.SECONDS);
    }
    
    @Override
    public void scheduleOnce(final Runnable job, String jobName, final long delay) {
        ExecutorService threadExecutor = Executors.newSingleThreadExecutor(ThreadFactories.namedThreadFactory(jobName, ThreadFactories.Type.DAEMON));
        threadExecutor.submit(new Runnable() {
            @SuppressWarnings("serial")
            @Override
            public void run() {
                try {
                    Thread.sleep(delay * 1000);
                    job.run();
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(AbstractLocalScheduler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

}
