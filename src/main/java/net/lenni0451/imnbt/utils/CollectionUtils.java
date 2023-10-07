package net.lenni0451.imnbt.utils;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionUtils {

    /**
     * Sort a map.
     *
     * @param map        The map to sort
     * @param comparator The comparator to use
     * @param <K>        The key type
     * @param <V>        The value type
     * @return The sorted map
     */
    public static <K, V> LinkedHashMap<K, V> sort(final Map<K, V> map, final Comparator<Map.Entry<K, V>> comparator) {
        return map.entrySet().stream().sorted(comparator).collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
    }

}
