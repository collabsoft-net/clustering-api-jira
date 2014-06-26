/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.collabsoft.clustering.jira.scheduler;

import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Map;

/**
 *
 * @author collabsoft
 */
public interface ClusteredTask {

    public JobRunnerResponse execute(Map<String, Object> map);
    
}
