
package net.collabsoft.clustering.jira.cache;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.util.BuildUtilsInfoImpl;
import com.atlassian.jira.util.system.VersionNumber;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCacheManager implements CacheManager {

    @Autowired
    private com.atlassian.cache.CacheManager clusteredCacheManager;
    
    // ----------------------------------------------------------------------------------------------- Constructor

    public AbstractCacheManager() {
        
    }
    
    // ----------------------------------------------------------------------------------------------- Getters & Setters

    public abstract Object getCacheObject(String key);
    public abstract CacheSettings getDefaultCacheSettings();
    public CacheLoader getDefaultCacheLoader() {
        return new AbstractCacheLoader() {
            @Override
            public Object getObjectForKey(String key) {
                return getCacheObject(key);
            }
        };
    }

    public Cache getCache(String key) {
        return getCache(key, getDefaultCacheSettings());
    }
    public Cache getCache(String key, CacheSettings settings) {
        return getCache(key, getDefaultCacheLoader(), settings);
    }
    public Cache getCache(String key, CacheLoader cacheLoader, CacheSettings settings) {
        if(isClusteredCache()) {
            return getClusteredCache(key, cacheLoader, settings);
        } else {
            return getLocalCache(key, cacheLoader, settings);
        }
    }

    
    // LOCAL CACHE
    
    
    public Cache getLocalCache(String key) {
        return getLocalCache(key, getDefaultCacheSettings());
    }
    public Cache getLocalCache(String key, CacheSettings settings) {
        return getLocalCache(key, getDefaultCacheLoader(), getDefaultCacheSettings());
    }
    public Cache getLocalCache(String key, CacheLoader cacheLoader, CacheSettings settings) {
        net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.getInstance();
        if(!cacheManager.cacheExists(key)) {
            net.sf.ehcache.config.CacheConfiguration config = getLocalCacheSettings(settings);
            config.setName(key);
            cacheManager.addCache(new net.sf.ehcache.Cache(config));
        }
        
        net.sf.ehcache.Cache cache = cacheManager.getCache(key);
        final net.sf.ehcache.Cache immutableCache = cache;
        final CacheLoader immutableCacheLoader = cacheLoader;

        return new Cache() {
            public Object getEntry(String key) {
                if(!immutableCache.isKeyInCache(key)) {
                    Object result = immutableCacheLoader.getCacheLoader().load(key);
                    putEntry(key, result);
                }
                
                net.sf.ehcache.Element entry = immutableCache.get(key);
                if(entry != null) {
                    return entry.getObjectValue();
                } else {
                    return immutableCacheLoader.getCacheLoader().load(key);
                }
            }

            public void putEntry(String key, Object value) {
                immutableCache.put(new net.sf.ehcache.Element(key, value));
            }

            public void clear() {
                immutableCache.removeAll();
            }
        };
    }
    
    public net.sf.ehcache.config.CacheConfiguration getLocalCacheSettings(CacheSettings settings) {
        if(settings == null) { settings = getDefaultCacheSettings(); }
        net.sf.ehcache.config.CacheConfiguration builder = new net.sf.ehcache.config.CacheConfiguration();
         
        // ExpireAfterRead
        if(settings.isExpireAfterAccess()) {
            builder.setTimeToIdleSeconds(TimeUnit.SECONDS.convert(settings.getExpireAfterAccessInterval(), settings.getExpireAfterAccessTimeUnit()));
        }

        // ExpireAfterWrite
        if(settings.isExpireAfterWrite()) {
            builder.setTimeToLiveSeconds(TimeUnit.SECONDS.convert(settings.getExpireAfterWriteInterval(), settings.getExpireAfterWriteTimeUnit()));
        }
         
        // MaxEntries
        if(settings.hasEntryLimit()) {
            builder.setMaxEntriesLocalHeap(settings.getMaxEntries());
        }
         
        return builder;
    }

    
    // CLUSTERED CACHE
    
    
    public Cache getClusteredCache(String key, CacheLoader cacheLoader, CacheSettings settings) {
        com.atlassian.cache.CacheManager cacheManager = ComponentAccessor.getComponent(com.atlassian.cache.CacheManager.class);
        com.atlassian.cache.Cache cache = cacheManager.getCache(key);
        if(cache == null) { cache = cacheManager.getCache(key, cacheLoader.getCacheLoader(), getClusteredCacheSettings(settings)); }
        final com.atlassian.cache.Cache immutableCache = cache;
        final com.atlassian.cache.CacheLoader immutableCacheLoader = cacheLoader.getCacheLoader();
        
        return new Cache() {
            public Object getEntry(String key) {
                if(!immutableCache.getKeys().contains(key)) {
                    Object entry = immutableCacheLoader.load(key);
                    putEntry(key, entry);
                }
                Object result = immutableCache.get(key);
                return result;
            }

            public void putEntry(String key, Object value) {
                immutableCache.put(key, value);
            }

            public void clear() {
                immutableCache.removeAll();
            }
        };
    }
    
    public com.atlassian.cache.CacheSettings getClusteredCacheSettings(CacheSettings settings) {
        if(settings == null) { settings = getDefaultCacheSettings(); }
        com.atlassian.cache.CacheSettingsBuilder builder = new com.atlassian.cache.CacheSettingsBuilder();
         
        // ExpireAfterRead
        if(settings.isExpireAfterAccess()) {
            builder.expireAfterAccess(settings.getExpireAfterAccessInterval(), settings.getExpireAfterAccessTimeUnit());
        }

        // ExpireAfterWrite
        if(settings.isExpireAfterWrite()) {
            builder.expireAfterWrite(settings.getExpireAfterWriteInterval(), settings.getExpireAfterWriteTimeUnit());
        }
         
        // MaxEntries
        if(settings.hasEntryLimit()) {
            builder.maxEntries(settings.getMaxEntries());
        }
         
        // Flushable
        if(settings.isFlushable()) {
            builder.flushable();
        } else {
            builder.unflushable();
        }
         
        // Local cache
        if(settings.isLocal()) {
            builder.local();
        } else {
            builder.remote();
        }
         
        return builder.build();
    }

    // ----------------------------------------------------------------------------------------------- Public methods


    // ----------------------------------------------------------------------------------------------- Private methods

    

    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

    private boolean isClusteredCache() {
        String version = new BuildUtilsInfoImpl().getVersion();
        VersionNumber versionNumber = new VersionNumber(version);
        if(versionNumber.isGreaterThanOrEquals(new VersionNumber(JIRA_MIN_MAJOR_VERSION + "." + JIRA_MIN_MINOR_VERSION))) {
            com.atlassian.cache.CacheManager cacheManager = ComponentAccessor.getComponentOfType(com.atlassian.cache.CacheManager.class);
            if(cacheManager == null) { cacheManager = clusteredCacheManager; }
            return (cacheManager != null);
        } else {
            return false;
        }
    }

}
