package io.curity.oauth.core.state;

import io.curity.oauth.core.util.event.Event;

/**
 * Data class representing the OAuth Server configuration state.
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

    @Override
    public String toString() {
        return "OAuthServerState{" +
                "baseUrl='" + baseUrl + '\'' +
                ", authorizeEndpoint='" + authorizeEndpoint + '\'' +
                ", tokenEndpoint='" + tokenEndpoint + '\'' +
                '}';
    }
}
