package se.curity.oauth.core.util;

import se.curity.oauth.app.MainApplication;
import se.curity.oauth.core.state.GeneralState;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.state.SslState;

import java.util.prefs.Preferences;

public final class UserPreferences
{
    private static final Preferences _preferences = Preferences.userNodeForPackage(MainApplication.class);

    private static final String BASE_URL_PREFERENCE_KEY = "BASE_URL";
    private static final String AUTHZ_ENDPOINT_PREFERENCE_KEY = "AUTHZ_ENDPOINT";
    private static final String TOKEN_ENDPOINT_PREFERENCE_KEY = "TOKEN_ENDPOINT";

    private static final String BASE_URL = "https://localhost:8443";
    private static final String TOKEN_ENDPOINT = "/oauth/token";

    private static final String VERBOSE = "VERBOSE";

    private static final String AUTHZ_ENDPOINT = "/oauth/authorize";
    private static final String IGNORE_SSL = "IGNORE_SSL";
    private static final String TRUSTSTORE_FILE = "TRUSTORE_FILE";
    private static final String TRUSTSTORE_PASSWORD = "TRUSTSTORE_PASSWORD";
    private static final String KEYSTORE_FILE = "KEYSTORE_FILE";
    private static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";

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
        boolean ignoreSSL = _preferences.getBoolean(IGNORE_SSL, true);
        String trustoreFile = _preferences.get(TRUSTSTORE_FILE, "");
        String trustorePassword = _preferences.get(TRUSTSTORE_PASSWORD, "");
        String keystoreFile = _preferences.get(KEYSTORE_FILE, "");
        String keystorePassword = _preferences.get(KEYSTORE_PASSWORD, "");

        return new SslState(ignoreSSL, trustoreFile, trustorePassword, keystoreFile, keystorePassword);
    }

    public GeneralState getGeneralPreferences()
    {
        boolean verbose = _preferences.getBoolean(VERBOSE, true);

        return new GeneralState(verbose);
    }

    public void putGeneralSettings(GeneralState generalState)
    {
        _preferences.putBoolean(VERBOSE, generalState.isVerbose());
    }

    public void putSslState(SslState sslState)
    {
        _preferences.putBoolean(IGNORE_SSL, sslState.isIgnoreSSL());
        _preferences.put(TRUSTSTORE_FILE, sslState.getTrustStoreFile());
        _preferences.put(TRUSTSTORE_PASSWORD, sslState.getTrustStorePassword()); // TODO: Encrypt
        _preferences.put(KEYSTORE_FILE, sslState.getKeystoreFile());
        _preferences.put(KEYSTORE_PASSWORD, sslState.getKeystorePassword()); // TODO: Encrypt
    }
}
