package se.curity.oauth.core.state;

import se.curity.oauth.core.util.Validatable;
import se.curity.oauth.core.util.event.Event;

import java.util.Collections;
import java.util.List;

/**
 * Data class representing the OAuth Server configuration state.
 */
public class OAuthServerState extends Validatable implements Event
{

    private final String baseUrl;
    private final String authorizeEndpoint;
    private final String tokenEndpoint;

    public static OAuthServerState invalid(List<String> errors)
    {
        return new OAuthServerState("", "", "", errors);
    }

    public static OAuthServerState validState(String baseUrl, String authorizeEndpoint, String tokenEndpoint)
    {
        return new OAuthServerState(baseUrl, authorizeEndpoint, tokenEndpoint, Collections.emptyList());
    }

    private OAuthServerState(String baseUrl, String authorizeEndpoint, String tokenEndpoint,
                             List<String> errors)
    {
        super(errors);
        this.baseUrl = baseUrl;
        this.authorizeEndpoint = authorizeEndpoint;
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public String getAuthorizeEndpoint()
    {
        return authorizeEndpoint;
    }

    public String getTokenEndpoint()
    {
        return tokenEndpoint;
    }

    @Override
    public String toString()
    {
        if (isValid())
        {
            return "OAuthServerState{" +
                    "baseUrl='" + baseUrl + '\'' +
                    ", authorizeEndpoint='" + authorizeEndpoint + '\'' +
                    ", tokenEndpoint='" + tokenEndpoint + '\'' +
                    '}';
        }
        else
        {
            return "OAuthServerState{errors=" + getValidationErrors() + '}';
        }
    }
}
