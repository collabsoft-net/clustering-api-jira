
package net.collabsoft.clustering.jira.cache;

import java.util.concurrent.TimeUnit;

public interface CacheSettings {

    // IMPLEMENTING DEFAULT LIMITS SPECIFIED BY ATLASSIAN
    //https://developer.atlassian.com/pages/viewpage.action?pageId=2031764
    public static final int DEFAULT_MAX_ENTRIES = 1000;
    public static final int DEFAULT_EXPIRE_AFTER_ACCESS_INTERVAL = 1;
    public static final TimeUnit DEFAULT_EXPIRE_AFTER_ACCESS_TIMEUNIT = TimeUnit.HOURS;
    
    public boolean isExpireAfterAccess();
    public void setExpireAfterAccess(boolean expireAfterAccess);
    public int getExpireAfterAccessInterval();
    public void setExpireAfterAccessInterval(int expireAfterAccessInterval);
    public TimeUnit getExpireAfterAccessTimeUnit();
    public void setExpireAfterAccessTimeUnit(TimeUnit expireAfterAccessTimeUnit);

    public boolean isExpireAfterWrite();
    public void setExpireAfterWrite(boolean expireAfterWrite);
    public int getExpireAfterWriteInterval();
    public void setExpireAfterWriteInterval(int expireAfterWriteInterval);
    public TimeUnit getExpireAfterWriteTimeUnit();
    public void setExpireAfterWriteTimeUnit(TimeUnit expireAfterWriteTimeUnit);

    public boolean isRefreshAfterWrite();
    public void setRefreshAfterWrite(boolean eefreshAfterWrite);
    public int getRefreshAfterWriteInterval();
    public void setRefreshAfterWriteInterval(int refreshAfterWriteInterval);
    public TimeUnit getRefreshAfterWriteTimeUnit();
    public void setRefreshAfterWriteTimeUnit(TimeUnit refreshAfterWriteTimeUnit);
    
    public boolean hasEntryLimit();
    public void setEntryLimit(boolean entryLimit);
    public int getMaxEntries();
    public void setMaxEntries(int maxEntries);
    
    public boolean isFlushable();
    public void setFlushable(boolean flushable);

    public boolean isLocal();
    public void setLocal(boolean local);
    
}
