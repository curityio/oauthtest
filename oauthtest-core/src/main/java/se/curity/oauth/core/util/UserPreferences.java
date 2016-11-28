package se.curity.oauth.core.util;

import se.curity.oauth.core.MainApplication;
import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.state.GeneralState;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.state.SslState;

import java.util.prefs.Preferences;

import static se.curity.oauth.core.state.SslState.SslOption.TRUST_OAUTH_SERVER_CERTIFICATE;

public final class UserPreferences
{
    private static final String CODE_FLOW_RESPONSE_TYPE_PREFERENCE_KEY = "CODE_FLOW_RESPONSE_TYPE_PREFERENCE_KEY";
    private static final String CODE_FLOW_CLIENT_ID_PREFERENCE_KEY = "CODE_FLOW_CLIENT_ID_PREFERENCE_KEY";
    private static final String CODE_FLOW_REDIRECT_URI_PREFERENCE_KEY = "CODE_FLOW_REDIRECT_URI_PREFERENCE_KEY";
    private static final String CODE_FLOW_SCOPE_PREFERENCE_KEY = "CODE_FLOW_SCOPE_PREFERENCE_KEY";
    private static final String CODE_FLOW_STATE_PREFERENCE_KEY = "CODE_FLOW_STATE_PREFERENCE_KEY";

    private final Preferences _preferences = Preferences.userNodeForPackage(MainApplication.class);
    private static final String BASE_URL_PREFERENCE_KEY = "BASE_URL";

    private static final String AUTHZ_ENDPOINT_PREFERENCE_KEY = "AUTHZ_ENDPOINT";
    private static final String TOKEN_ENDPOINT_PREFERENCE_KEY = "TOKEN_ENDPOINT";
    private static final String BASE_URL = "https://localhost:8443";

    private static final String TOKEN_ENDPOINT = "/oauth/token";
    private static final String VERBOSE = "VERBOSE";
    private static final String MAX_NOTIFICATION_ROWS = "MAX_NOTIFICATION_ROWS";
    private static final String AUTHZ_ENDPOINT = "/oauth/authorize";
    private static final String SSL_OPTION = "SSL_OPTION";
    private static final String TRUSTSTORE_FILE = "TRUSTORE_FILE";
    private static final String TRUSTSTORE_PASSWORD = "TRUSTSTORE_PASSWORD";
    private static final String KEYSTORE_FILE = "KEYSTORE_FILE";
    private static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";

    public CodeFlowAuthzState getCodeFlowPreferences()
    {
        String responseType = _preferences.get(CODE_FLOW_RESPONSE_TYPE_PREFERENCE_KEY, "code");
        String clientId = _preferences.get(CODE_FLOW_CLIENT_ID_PREFERENCE_KEY, "");
        String redirectUri = _preferences.get(CODE_FLOW_REDIRECT_URI_PREFERENCE_KEY, "se.curity.oauthtest://");
        String scope = _preferences.get(CODE_FLOW_SCOPE_PREFERENCE_KEY, "");
        String state = _preferences.get(CODE_FLOW_STATE_PREFERENCE_KEY, "");

        return CodeFlowAuthzState.validState(responseType, clientId, redirectUri, scope, state);
    }

    public void putCodeFlowPreferences(CodeFlowAuthzState codeFlowAuthzState)
    {
        _preferences.put(CODE_FLOW_RESPONSE_TYPE_PREFERENCE_KEY, codeFlowAuthzState.getResponseType());
        _preferences.put(CODE_FLOW_CLIENT_ID_PREFERENCE_KEY, codeFlowAuthzState.getClientId());
        _preferences.put(CODE_FLOW_REDIRECT_URI_PREFERENCE_KEY, codeFlowAuthzState.getRedirectUri());
        _preferences.put(CODE_FLOW_SCOPE_PREFERENCE_KEY, codeFlowAuthzState.getScope());
        _preferences.put(CODE_FLOW_STATE_PREFERENCE_KEY, codeFlowAuthzState.getState());
    }

    public OAuthServerState getOAuthServerPreferences()
    {
        String baseUrl = _preferences.get(BASE_URL_PREFERENCE_KEY, BASE_URL);
        String authorizeEndpoint = _preferences.get(AUTHZ_ENDPOINT_PREFERENCE_KEY, AUTHZ_ENDPOINT);
        String tokenEndpoint = _preferences.get(TOKEN_ENDPOINT_PREFERENCE_KEY, TOKEN_ENDPOINT);

        return OAuthServerState.validState(baseUrl, authorizeEndpoint, tokenEndpoint);
    }

    public void putOAuthServerPreferences(OAuthServerState oauthServerState)
    {
        _preferences.put(BASE_URL_PREFERENCE_KEY, oauthServerState.getBaseUrl());
        _preferences.put(AUTHZ_ENDPOINT_PREFERENCE_KEY, oauthServerState.getAuthorizeEndpoint());
        _preferences.put(TOKEN_ENDPOINT_PREFERENCE_KEY, oauthServerState.getTokenEndpoint());
    }

    public SslState getSslPreferences()
    {
        // ignore SSL by default, otherwise we would have to set a default keystore which is impossible
        SslState.SslOption sslOption = SslState.SslOption.valueOf(
                _preferences.get(SSL_OPTION, TRUST_OAUTH_SERVER_CERTIFICATE.name()));

        String trustoreFile = _preferences.get(TRUSTSTORE_FILE, "");
        String trustorePassword = _preferences.get(TRUSTSTORE_PASSWORD, "");
        String keystoreFile = _preferences.get(KEYSTORE_FILE, "");
        String keystorePassword = _preferences.get(KEYSTORE_PASSWORD, "");

        return new SslState(sslOption, trustoreFile, trustorePassword, keystoreFile, keystorePassword);
    }

    public GeneralState getGeneralPreferences()
    {
        boolean verbose = _preferences.getBoolean(VERBOSE, GeneralState.DEFAULT_VERBOSE);
        int maximumNotificationRows = _preferences.getInt(
                MAX_NOTIFICATION_ROWS,
                GeneralState.DEFAULT_MAX_NOTIFICATION_ROWS);

        return new GeneralState(verbose, maximumNotificationRows);
    }

    public void putGeneralPreferences(GeneralState generalState)
    {
        _preferences.putBoolean(VERBOSE, generalState.isVerbose());
        _preferences.putInt(MAX_NOTIFICATION_ROWS, generalState.getMaximumNotificationRows());
    }

    public void putSslPreferences(SslState sslState)
    {
        _preferences.put(SSL_OPTION, sslState.getSslOption().name());
        _preferences.put(TRUSTSTORE_FILE, sslState.getTrustStoreFile());
        _preferences.put(TRUSTSTORE_PASSWORD, sslState.getTrustStorePassword()); // TODO: Encrypt
        _preferences.put(KEYSTORE_FILE, sslState.getKeystoreFile());
        _preferences.put(KEYSTORE_PASSWORD, sslState.getKeystorePassword()); // TODO: Encrypt
    }

}
