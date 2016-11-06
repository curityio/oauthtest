package se.curity.oauth.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * List helper functions.
 */
public class ListUtils
{

    public static <T> List<T> append(List<T> a, List<T> b)
    {
        List<T> result = new ArrayList<>(a.size() + b.size());
        result.addAll(a);
        result.addAll(b);
        return result;
    }

    public static String joinStringsWith(String itemPrefix, List<String> items)
    {
        return String.join("", items.stream()
                .map(it -> itemPrefix + it)
                .collect(Collectors.toList()));
    }

}
