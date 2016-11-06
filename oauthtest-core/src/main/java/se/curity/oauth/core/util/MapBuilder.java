package se.curity.oauth.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Extremely simple builder of HashMaps.
 */
public class MapBuilder<K, V>
{

    private final Map<K, V> map = new HashMap<>();

    public MapBuilder<K, V> put(K key, V value)
    {
        map.put(key, value);
        return this;
    }

    public Map<K, V> getMap()
    {
        return map;
    }

}
