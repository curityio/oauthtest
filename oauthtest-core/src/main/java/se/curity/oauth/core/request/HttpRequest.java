package se.curity.oauth.core.request;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.UnmodifiableMultivaluedMap;
import se.curity.oauth.core.util.MapBuilder;
import se.curity.oauth.core.util.event.Event;

import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collections;
import java.util.Map;

/**
 * Simplified representation of a HTTP Request that is enough to represent any OAuth request.
 */
public abstract class HttpRequest implements Event {

    private final String method;
    private final String host;
    private final String path;
    private final Map<String, Object> headers;
    private final MultivaluedMap<String, String> query;

    @Nullable
    private final String body;

    HttpRequest( String method,
                 String host,
                 String path,
                 Map<String, Object> headers,
                 MultivaluedMap<String, String> query ) {
        this( method, host, path, headers, query, null );
    }

    HttpRequest( String method,
                 String host,
                 String path,
                 Map<String, Object> headers,
                 MultivaluedMap<String, String> query,
                 @Nullable String body ) {
        this.method = method;
        this.host = host;
        this.path = path;
        this.headers = Collections.unmodifiableMap( headers );
        this.query = new UnmodifiableMultivaluedMap<>( query );
        this.body = body;
    }

    public ClientResponse send() {
        Client client = Client.create();
        WebResource.Builder requestBuilder = client
                .resource( host )
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
        // TODO
        return "curl -X " + method + " " + host;
    }

    static MapBuilder<String, Object> oauthBasicHeaders() {
        return new MapBuilder<String, Object>()
                .put( "Accept", "application/json" );
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", host='" + host + '\'' +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                ", query=" + query +
                ", body='" + body + '\'' +
                '}';
    }
}
