package se.curity.oauth.core.state;

import se.curity.oauth.core.util.Validatable;

import java.util.Collections;
import java.util.List;

public class CodeFlowAuthzState extends Validatable {

    private final String responseType;
    private final String clientId;
    private final String redirectUri;
    private final String scope;
    private final String state;

    public static CodeFlowAuthzState invalid( List<String> errors ) {
        return new CodeFlowAuthzState( "", "", "", "", "", errors );
    }

    public static CodeFlowAuthzState validState( String responseType, String clientId,
                                                 String redirectUri, String scope, String state ) {
        return new CodeFlowAuthzState( responseType, clientId, redirectUri, scope, state, Collections.emptyList() );
    }

    private CodeFlowAuthzState( String responseType, String clientId, String redirectUri,
                                String scope, String state, List<String> errors ) {
        super( errors );
        this.responseType = responseType;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.scope = scope;
        this.state = state;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public String getState() {
        return state;
    }

}
