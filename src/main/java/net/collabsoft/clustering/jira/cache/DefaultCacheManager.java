package net.collabsoft.clustering.jira.cache;

import com.google.common.collect.Lists;
import java.util.List;

public class DefaultCacheManager extends AbstractCacheManager {

    List<CacheLoader> cacheLoaders;
    
    // ----------------------------------------------------------------------------------------------- Constructor

    public DefaultCacheManager() {
        cacheLoaders = Lists.newArrayList();
    }

    // ----------------------------------------------------------------------------------------------- Getters & Setters

    @Override
    public Object getCacheObject(String key) {
        for(CacheLoader cacheLoader : cacheLoaders) {
            Object result = cacheLoader.getObjectForKey(key);
            if(result != null) { return result; }
        }
        return null;
    }

    @Override
    public CacheSettings getDefaultCacheSettings() {
        return new DefaultCacheSettings();
    }

    // ----------------------------------------------------------------------------------------------- Public methods

    public void registerCacheLoader(CacheLoader cacheLoader) {
        cacheLoaders.add(cacheLoader);
    }

    // ----------------------------------------------------------------------------------------------- Private methods


    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

    

}
