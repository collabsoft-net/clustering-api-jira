
package net.collabsoft.clustering.jira.scheduler;

import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Map;

public abstract class AbstractClusteredTask implements ClusteredTask {

    // ----------------------------------------------------------------------------------------------- Constructor

    public AbstractClusteredTask() {
    }

    public abstract JobRunnerResponse execute(Map<String, Object> map);

}
