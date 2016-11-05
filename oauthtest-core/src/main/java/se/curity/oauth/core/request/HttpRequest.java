package se.curity.oauth.core.request;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.UnmodifiableMultivaluedMap;
import se.curity.oauth.core.util.MapBuilder;
import se.curity.oauth.core.util.event.Event;

import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Simplified representation of a HTTP Request that is enough to represent any OAuth request.
 */
public abstract class HttpRequest implements Event {

    private final String method;
    private final String baseUrl;
    private final String path;
    private final Map<String, Object> headers;
    private final MultivaluedMap<String, String> query;

    @Nullable
    private final String body;

    private final Function<Object, String> urlEncoder = obj -> {
        try {
            return URLEncoder.encode( obj.toString().trim(), "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( "This runtime does not support UTF-8. Cannot run the application." );
        }
    };

    HttpRequest( String method,
                 String baseUrl,
                 String path,
                 Map<String, Object> headers,
                 MultivaluedMap<String, String> query ) {
        this( method, baseUrl, path, headers, query, null );
    }

    HttpRequest( String method,
                 String baseUrl,
                 String path,
                 Map<String, Object> headers,
                 MultivaluedMap<String, String> query,
                 @Nullable String body ) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.path = path;
        this.headers = Collections.unmodifiableMap( headers );
        this.query = new UnmodifiableMultivaluedMap<>( query );
        this.body = body;
    }

    public ClientResponse send() {
        Client client = Client.create();
        WebResource.Builder requestBuilder = client
                .resource( baseUrl )
                .path( path )
                .queryParams( query )
                .getRequestBuilder();

        for (Map.Entry<String, Object> header : headers.entrySet()) {
            requestBuilder = requestBuilder.header( header.getKey(), header.getValue() );
        }

        return ( body == null ) ?
                requestBuilder.method( method, ClientResponse.class ) :
                requestBuilder.method( method, ClientResponse.class, body );
    }

    public String toCurl() {
        String curlHeaders = String.join( " ", headers.entrySet().stream()
                .map( it -> "-H \"" + it.getKey() + ": " + it.getValue() + "\"" )
                .collect( Collectors.toList() ) );

        String queryString = query.isEmpty() ? "" :
                "?" + String.join( "&", query.entrySet().stream()
                        .flatMap( it -> it.getValue().stream()
                                .map( v -> urlEncoder.apply( it.getKey() ) + "=" + urlEncoder.apply( v ) ) )
                        .collect( Collectors.toList() ) );

        String absolutePath = path.startsWith( "/" ) ? path : "/" + path;
        String fullUrl = "\"" + baseUrl + absolutePath + queryString + "\"";

        return String.join( " ", Arrays.asList( "curl -X", method, curlHeaders, fullUrl ) );
    }

    static MapBuilder<String, Object> oauthBasicHeaders() {
        return new MapBuilder<String, Object>()
                .put( "Accept", "application/json" );
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                ", query=" + query +
                ", body='" + body + '\'' +
                '}';
    }
}
