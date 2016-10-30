package com.athaydes.oauth.core.state;

import com.athaydes.oauth.core.util.event.Event;

/**
 *
 */
public class OAuthServerState implements Event {

    private final String baseUrl;
    private final String authorizeEndpoint;
    private final String tokenEndpoint;

    public OAuthServerState( String baseUrl, String authorizeEndpoint, String tokenEndpoint ) {
        this.baseUrl = baseUrl;
        this.authorizeEndpoint = authorizeEndpoint;
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAuthorizeEndpoint() {
        return authorizeEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }
}
