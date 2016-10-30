package com.athaydes.oauth.core.state;

public class CodeFlowAuthzState {
    private final String responseType;
    private final String clientId;
    private final String redirectUri;
    private final String scope;
    private final String state;

    public CodeFlowAuthzState( String responseType, String clientId, String redirectUri, String scope, String state ) {
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
