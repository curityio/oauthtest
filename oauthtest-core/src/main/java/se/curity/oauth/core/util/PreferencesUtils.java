package se.curity.oauth.core.util;

import se.curity.oauth.core.MainApplication;
import se.curity.oauth.core.state.OAuthServerState;

import java.util.prefs.Preferences;

public final class PreferencesUtils
{
    private static final Preferences _preferences = Preferences.userNodeForPackage(MainApplication.class);

    private static final String BASE_URL_PREFERENCE_KEY = "BASE_URL";
    private static final String AUTHZ_ENDPOINT_PREFERENCE_KEY = "AUTHZ_ENDPOINT";
    private static final String TOKEN_ENDPOINT_PREFERENCE_KEY = "TOKEN_ENDPOINT";

    private static final String BASE_URL = "https://localhost:8443";
    private static final String TOKEN_ENDPOINT = "/oauth/token";
    private static final String AUTHZ_ENDPOINT = "/oauth/authorize";

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
}
