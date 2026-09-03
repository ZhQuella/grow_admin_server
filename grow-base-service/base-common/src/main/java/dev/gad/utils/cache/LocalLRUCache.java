package dev.gad.utils.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LIANGQI
 * @version 1.0
 * @Desc
 * @time 2023/12/16 15:44
 */

public class LocalLRUCache <K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LocalLRUCache(int capacity) {
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

