package se.curity.oauth.core.request;

import com.sun.jersey.core.util.StringKeyStringValueIgnoreCaseMultivaluedMap;
import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.state.OAuthServerState;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

public class CodeFlowAuthorizeRequest extends HttpRequest
{

    public CodeFlowAuthorizeRequest(OAuthServerState serverState, CodeFlowAuthzState state)
    {
        super(HttpMethod.GET,
                serverState.getBaseUrl(),
                serverState.getAuthorizeEndpoint(),
                oauthBasicHeaders().getMap(),
                queryParameters(state));
    }

    private static MultivaluedMap<String, String> queryParameters(CodeFlowAuthzState state)
    {
        StringKeyStringValueIgnoreCaseMultivaluedMap result =
                new StringKeyStringValueIgnoreCaseMultivaluedMap();

        result.putSingleObject("response_type", state.getResponseType());

        String clientId = state.getClientId();
        String redirectUri = state.getRedirectUri();
        String scope = state.getScope();
        String stateParam = state.getState();

        if (!clientId.isEmpty()) result.putSingleObject("client_id", clientId);
        if (!redirectUri.isEmpty()) result.putSingleObject("redirect_uri", redirectUri);
        if (!scope.isEmpty()) result.putSingleObject("scope", scope);
        if (!stateParam.isEmpty()) result.putSingleObject("state", stateParam);

        return result;
    }
}
