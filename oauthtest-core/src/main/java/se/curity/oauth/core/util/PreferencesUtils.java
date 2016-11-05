package se.curity.oauth.core.util;

import se.curity.oauth.core.MainApplication;
import se.curity.oauth.core.state.OAuthServerState;

import javax.annotation.Nullable;
import java.util.prefs.Preferences;

public class PreferencesUtils {

    private static final Preferences preferences = Preferences.userNodeForPackage( MainApplication.class );

    private static final String BASE_URL_PREFERENCE_KEY = "BASE_URL";
    private static final String AUTHZ_ENDPOINT_PREFERENCE_KEY = "AUTHZ_ENDPOINT";
    private static final String TOKEN_ENDPOINT_PREFERENCE_KEY = "TOKEN_ENDPOINT";

    private static final String BASE_URL = "https://localhost:8443";
    private static final String TOKEN_ENDPOINT = "/oauth/token";
    private static final String AUTHZ_ENDPOINT = "/oauth/authorize";

    @Nullable
    private static OAuthServerState oauthServerState;

    public static synchronized OAuthServerState getOAuthServerPreferences() {

        if ( oauthServerState == null ) {

            String baseUrl = preferences.get( BASE_URL_PREFERENCE_KEY, BASE_URL );
            String authorizeEndpoint = preferences.get( AUTHZ_ENDPOINT_PREFERENCE_KEY, AUTHZ_ENDPOINT );
            String tokenEndpoint = preferences.get( TOKEN_ENDPOINT_PREFERENCE_KEY, TOKEN_ENDPOINT );

            oauthServerState = new OAuthServerState( baseUrl, authorizeEndpoint, tokenEndpoint );
        }

        return oauthServerState;
    }

    public static void putOAuthServerPreferences(String baseUrl, String authorizeEndpoint, String tokenEndpoint) {

        preferences.put( BASE_URL_PREFERENCE_KEY, baseUrl );
        preferences.put( AUTHZ_ENDPOINT_PREFERENCE_KEY, authorizeEndpoint );
        preferences.put( TOKEN_ENDPOINT_PREFERENCE_KEY, tokenEndpoint) ;
    }
}
