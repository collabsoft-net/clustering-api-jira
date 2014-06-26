
package net.collabsoft.clustering.jira.cache;

public interface CacheManager {

    final int JIRA_MIN_MAJOR_VERSION = 6;
    final int JIRA_MIN_MINOR_VERSION = 2;

    public abstract Object getCacheObject(String key);
    public abstract CacheSettings getDefaultCacheSettings();
    public CacheLoader getDefaultCacheLoader();
    
    public Cache getCache(String key);
    public Cache getCache(String key, CacheSettings settings);
    public Cache getCache(String key, CacheLoader cacheLoader, CacheSettings settings);
    
}
