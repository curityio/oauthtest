package se.curity.oauth.core.request;

import org.glassfish.jersey.client.ClientProperties;
import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.state.SslState;

import javax.annotation.Nullable;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class CodeFlowAuthorizeRequest extends HttpRequest
{

    private final CodeFlowAuthzState _state;

    public CodeFlowAuthorizeRequest(OAuthServerState serverState, CodeFlowAuthzState state)
    {
        super(HttpMethod.GET,
                serverState.getBaseUrl(),
                serverState.getAuthorizeEndpoint(),
                oauthBasicHeaders().getMap(),
                queryParameters(state));
        _state = state;
    }

    @Override
    protected ClientBuilder createClientWith(@Nullable SslState sslState)
    {
        ClientBuilder builder = super.createClientWith(sslState);
        builder.property(ClientProperties.FOLLOW_REDIRECTS, false);
        return builder;
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
