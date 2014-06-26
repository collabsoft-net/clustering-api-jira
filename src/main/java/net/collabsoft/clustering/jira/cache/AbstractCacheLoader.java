
package net.collabsoft.clustering.jira.cache;

public abstract class AbstractCacheLoader implements CacheLoader {

    // ----------------------------------------------------------------------------------------------- Constructor

    public AbstractCacheLoader() {
    }

    // ----------------------------------------------------------------------------------------------- Getters & Setters

    public abstract Object getObjectForKey(String key);

    public com.atlassian.cache.CacheLoader getCacheLoader() {
        return new com.atlassian.cache.CacheLoader<String, Object>() {

            public Object load(String key) {
                return getObjectForKey(key);
            }
            
        };
    }
    
    // ----------------------------------------------------------------------------------------------- Public methods


    // ----------------------------------------------------------------------------------------------- Private methods


    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

}
