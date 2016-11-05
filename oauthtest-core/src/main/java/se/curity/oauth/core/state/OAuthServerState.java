package se.curity.oauth.core.state;

import se.curity.oauth.core.util.event.Event;

/**
 * Data class representing the OAuth Server configuration state.
 */
public class OAuthServerState implements Event {

    private final String baseUrl;
    private final String authorizeEndpoint;
    private final String tokenEndpoint;

    private static final OAuthServerState INVALID = new OAuthServerState( "", "", "" );

    public static OAuthServerState invalid() {
        return INVALID;
    }

    public static OAuthServerState validState( String baseUrl, String authorizeEndpoint, String tokenEndpoint ) {
        return new OAuthServerState( baseUrl, authorizeEndpoint, tokenEndpoint );
    }

    private OAuthServerState( String baseUrl, String authorizeEndpoint, String tokenEndpoint ) {
        this.baseUrl = baseUrl;
        this.authorizeEndpoint = authorizeEndpoint;
        this.tokenEndpoint = tokenEndpoint;
    }

    public boolean isValid() {
        return this != INVALID;
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
