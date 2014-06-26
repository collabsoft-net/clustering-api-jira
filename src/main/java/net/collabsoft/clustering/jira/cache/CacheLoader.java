
package net.collabsoft.clustering.jira.cache;

public interface CacheLoader {
    
    public Object getObjectForKey(String key);
    public com.atlassian.cache.CacheLoader getCacheLoader();
    
}
