package edu.sjsu.cmpe.cache.client;

import java.util.SortedMap;


/**
 * Cache Service Interface
 * 
 */
public interface CacheServiceInterface {
    public String get(long key);

    public void put(long key, String value);
    
    public String getServer(Object key);
    
    public SortedMap<Integer, String> getCircle();
}
