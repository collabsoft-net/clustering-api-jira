package net.collabsoft.clustering.jira.cache;

import java.util.concurrent.TimeUnit;

public class DefaultCacheSettings implements CacheSettings {
    
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

    public DefaultCacheSettings() {
        
    }

    // ----------------------------------------------------------------------------------------------- Getters & Setters
    public boolean isExpireAfterAccess() {
        return expireAfterAccess;
    }

    public void setExpireAfterAccess(boolean expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
    }

    public int getExpireAfterAccessInterval() {
        return expireAfterAccessInterval;
    }

    public void setExpireAfterAccessInterval(int expireAfterAccessInterval) {
        this.expireAfterAccessInterval = expireAfterAccessInterval;
    }

    public TimeUnit getExpireAfterAccessTimeUnit() {
        return expireAfterAccessTimeUnit;
    }

    public void setExpireAfterAccessTimeUnit(TimeUnit expireAfterAccessTimeUnit) {
        this.expireAfterAccessTimeUnit = expireAfterAccessTimeUnit;
    }

    public boolean isExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(boolean expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public int getExpireAfterWriteInterval() {
        return expireAfterWriteInterval;
    }

    public void setExpireAfterWriteInterval(int expireAfterWriteInterval) {
        this.expireAfterWriteInterval = expireAfterWriteInterval;
    }

    public TimeUnit getExpireAfterWriteTimeUnit() {
        return expireAfterWriteTimeUnit;
    }

    public void setExpireAfterWriteTimeUnit(TimeUnit expireAfterWriteTimeUnit) {
        this.expireAfterWriteTimeUnit = expireAfterWriteTimeUnit;
    }

    public boolean isRefreshAfterWrite() {
        return refreshAfterWrite;
    }

    public void setRefreshAfterWrite(boolean refreshAfterWrite) {
        this.refreshAfterWrite = refreshAfterWrite;
    }

    public int getRefreshAfterWriteInterval() {
        return refreshAfterWriteInterval;
    }

    public void setRefreshAfterWriteInterval(int refreshAfterWriteInterval) {
        this.refreshAfterWriteInterval = refreshAfterWriteInterval;
    }

    public TimeUnit getRefreshAfterWriteTimeUnit() {
        return refreshAfterWriteTimeUnit;
    }

    public void setRefreshAfterWriteTimeUnit(TimeUnit refreshAfterWriteTimeUnit) {
        this.refreshAfterWriteTimeUnit = refreshAfterWriteTimeUnit;
    }

    public boolean hasEntryLimit() {
        return entryLimit;
    }

    public void setEntryLimit(boolean entryLimit) {
        this.entryLimit = entryLimit;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    public boolean isFlushable() {
        return flushable;
    }

    public void setFlushable(boolean flushable) {
        this.flushable = flushable;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    
    // ----------------------------------------------------------------------------------------------- Public methods


    // ----------------------------------------------------------------------------------------------- Private methods


    // ----------------------------------------------------------------------------------------------- Private Getters & Setters

}
