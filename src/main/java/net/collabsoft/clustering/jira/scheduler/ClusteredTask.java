
package net.collabsoft.clustering.jira.scheduler;

import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Map;

public interface ClusteredTask {
    JobRunnerResponse execute(Map<String, Object> map);
}
