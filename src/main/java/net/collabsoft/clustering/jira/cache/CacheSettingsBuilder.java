package net.collabsoft.clustering.jira.cache;

import java.util.concurrent.TimeUnit;

public class CacheSettingsBuilder {

    // IMPLEMENTING DEFAULT LIMITS SPECIFIED BY ATLASSIAN
    //https://developer.atlassian.com/pages/viewpage.action?pageId=2031764
    private boolean expireAfterAccess = true;
    private int expireAfterAccessInterval = CacheSettings.DEFAULT_EXPIRE_AFTER_ACCESS_INTERVAL;
    private TimeUnit expireAfterAccessTimeUnit = CacheSettings.DEFAULT_EXPIRE_AFTER_ACCESS_TIMEUNIT;
    private boolean expireAfterWrite;
    private int expireAfterWriteInterval;
    private TimeUnit expireAfterWriteTimeUnit;
    private boolean refreshAfterWrite;
    private int refreshAfterWriteInterval;
    private TimeUnit refreshAfterWriteTimeUnit;
    private boolean entryLimit = true;
    private int maxEntries = CacheSettings.DEFAULT_MAX_ENTRIES;
    private boolean flushable;
    private boolean local;
    
    // ----------------------------------------------------------------------------------------------- Constructor

    public CacheSettingsBuilder() {
        
    }

    // ----------------------------------------------------------------------------------------------- Getters & Setters

    public CacheSettingsBuilder expireAfterAccess(int interval, TimeUnit timeUnit) {
        return expireAfterAccess(true).expireAfterAccessInterval(interval)
                                     .expireAfterAccessTimeUnit(timeUnit);
    }
    
    public CacheSettingsBuilder expireAfterAccess(boolean expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
        return this;
    }

    public CacheSettingsBuilder expireAfterAccessInterval(int expireAfterAccessInterval) {
        this.expireAfterAccessInterval = expireAfterAccessInterval;
        return this;
    }

    public CacheSettingsBuilder expireAfterAccessTimeUnit(TimeUnit expireAfterAccessTimeUnit) {
        this.expireAfterAccessTimeUnit = expireAfterAccessTimeUnit;
        return this;
    }

    public CacheSettingsBuilder expireAfterWrite(int interval, TimeUnit timeUnit) {
        return expireAfterWrite(true).expireAfterWriteInterval(interval)
                                     .expireAfterWriteTimeUnit(timeUnit);
    }
    
    public CacheSettingsBuilder expireAfterWrite(boolean expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        return this;
    }

    public CacheSettingsBuilder expireAfterWriteInterval(int expireAfterWriteInterval) {
        this.expireAfterWriteInterval = expireAfterWriteInterval;
        return this;
    }

    public CacheSettingsBuilder expireAfterWriteTimeUnit(TimeUnit expireAfterWriteTimeUnit) {
        this.expireAfterWriteTimeUnit = expireAfterWriteTimeUnit;
        return this;
    }

    public CacheSettingsBuilder entryLimit(boolean entryLimit) {
        this.entryLimit = entryLimit;
        return this;
    }

    public CacheSettingsBuilder maxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
        return this;
    }

    public CacheSettingsBuilder flushable(boolean flushable) {
        this.flushable = flushable;
        return this;
    }

    public CacheSettingsBuilder local(boolean local) {
        this.local = local;
        return this;
    }
    
    public CacheSettings build() {
        DefaultCacheSettings settings = new DefaultCacheSettings();
        settings.setEntryLimit(entryLimit);
        settings.setExpireAfterAccess(expireAfterAccess);
        settings.setExpireAfterAccessInterval(expireAfterAccessInterval);
        settings.setExpireAfterAccessTimeUnit(expireAfterAccessTimeUnit);
        settings.setExpireAfterWrite(expireAfterWrite);
        settings.setExpireAfterWriteInterval(expireAfterWriteInterval);
        settings.setExpireAfterWriteTimeUnit(expireAfterWriteTimeUnit);
        settings.setFlushable(flushable);
        settings.setLocal(local);
        settings.setMaxEntries(maxEntries);
        settings.setRefreshAfterWrite(refreshAfterWrite);
        settings.setRefreshAfterWriteInterval(refreshAfterWriteInterval);
        settings.setRefreshAfterWriteTimeUnit(refreshAfterWriteTimeUnit);
        return settings;
    };
    
    // ----------------------------------------------------------------------------------------------- Public methods


    // ----------------------------------------------------------------------------------------------- Private methods


    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

}
