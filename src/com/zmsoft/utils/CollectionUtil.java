package com.zmsoft.utils;

import java.util.Collection;
import java.util.Map;

/**
 * auth aboom
 * date 2018/5/7
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(String... args) {
        return args == null || args.length == 0;
    }

    public static <T> boolean isEmpty(T[] t) {
        return t == null || t.length == 0;
    }
}
