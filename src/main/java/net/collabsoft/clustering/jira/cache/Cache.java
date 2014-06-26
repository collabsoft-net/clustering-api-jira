
package net.collabsoft.clustering.jira.cache;

public interface Cache {
    
    public Object getEntry(String key);
    public void putEntry(String key, Object value);
    public void clear();
    
}