package se.curity.oauth.core.controller.flows.code;

import se.curity.oauth.core.request.HttpRequest;

import java.net.URI;

/**
 * Request that runs after the user authenticates during the authorization part of the OAuth code flow.
 */
public class CodeFlowAfterAuthenticationRequest extends HttpRequest
{


    CodeFlowAfterAuthenticationRequest(URI uri)
    {
        super("GET", uri.getScheme() + "://" + uri.getAuthority(), uri.getPath(),
                oauthBasicHeaders().getMap(),
                parseQueryParameters(uri.getQuery()));
    }

}
