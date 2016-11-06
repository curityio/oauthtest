package se.curity.oauth.core.request;

import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.state.OAuthServerState;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class CodeFlowAuthorizeRequest extends HttpRequest
{

    private final CodeFlowAuthzState _state;

    public CodeFlowAuthorizeRequest(OAuthServerState serverState, CodeFlowAuthzState state)
    {
        // TODO turn off follow-redirects!
        super(HttpMethod.GET,
                serverState.getBaseUrl(),
                serverState.getAuthorizeEndpoint(),
                oauthBasicHeaders().getMap(),
                queryParameters(state));
        _state = state;
    }

    public CodeFlowAuthzState getState()
    {
        return _state;
    }

    private static MultivaluedMap<String, String> queryParameters(CodeFlowAuthzState state)
    {
        MultivaluedMap<String, String> result =
                new MultivaluedHashMap<>();

        result.putSingle("response_type", state.getResponseType());

        String clientId = state.getClientId();
        String redirectUri = state.getRedirectUri();
        String scope = state.getScope();
        String stateParam = state.getState();

        if (!clientId.isEmpty()) result.putSingle("client_id", clientId);
        if (!redirectUri.isEmpty()) result.putSingle("redirect_uri", redirectUri);
        if (!scope.isEmpty()) result.putSingle("scope", scope);
        if (!stateParam.isEmpty()) result.putSingle("state", stateParam);

        return result;
    }
}
