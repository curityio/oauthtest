package io.curity.oauth.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ListUtils {

    public static <T> List<T> append( List<T> a, List<T> b ) {
        List<T> result = new ArrayList<>( a.size() + b.size() );
        result.addAll( a );
        result.addAll( b );
        return result;
    }

}
