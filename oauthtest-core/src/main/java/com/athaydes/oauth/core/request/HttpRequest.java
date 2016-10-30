package com.athaydes.oauth.core.request;

import com.athaydes.oauth.core.util.MapBuilder;
import com.athaydes.oauth.core.util.event.Event;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.UnmodifiableMultivaluedMap;

import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collections;
import java.util.Map;

/**
 *
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
        System.out.println( "Sending request" );
        Client client = Client.create();
        WebResource requestBuilder = client
                .resource( host )
                .path( path )
                .queryParams( query );

        for (Map.Entry<String, Object> header : headers.entrySet()) {
            requestBuilder.header( header.getKey(), header.getValue() );
        }

        ClientResponse response = ( body == null ) ?
                requestBuilder.method( method, ClientResponse.class ) :
                requestBuilder.method( method, ClientResponse.class, body );

        System.out.println( "Response: " + response );

        return response;
    }

    public String toCurl() {
        // TODO
        return "curl -X " + method + " " + host;
    }

    static MapBuilder<String, Object> oauthBasicHeaders() {
        return new MapBuilder<String, Object>()
                .put( "Accept", "application/json" );
    }

}
