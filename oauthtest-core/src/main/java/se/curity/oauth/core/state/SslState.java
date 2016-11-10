package se.curity.oauth.core.state;

import se.curity.oauth.core.util.event.Event;

/**
 * The state of the SSL settings.
 * <p>
 * The SSL settings are used to configure the HTTP client.
 */
public class SslState implements Event
{
    public enum SslOption {
        IGNORE_SSL,
        TRUST_OAUTH_SERVER_CERTIFICATE,
        USE_KEYSTORE
    }

    private final SslOption _sslOption;
    private final String _trustStoreFile;
    private final String _trustStorePassword;
    private final String _keystoreFile;
    private final String _keystorePassword;

    public SslState(SslOption sslOption, String trustStoreFile,
                    String trustStorePassword, String keystoreFile,
                    String keystorePassword)
    {
        _sslOption=sslOption;
        _trustStoreFile = trustStoreFile;
        _trustStorePassword = trustStorePassword;
        _keystoreFile = keystoreFile;
        _keystorePassword = keystorePassword;
    }

    public SslOption getSslOption()
    {
        return _sslOption;
    }

    public String getTrustStoreFile()
    {
        return _trustStoreFile;
    }

    public String getTrustStorePassword()
    {
        return _trustStorePassword;
    }

    public String getKeystoreFile()
    {
        return _keystoreFile;
    }

    public String getKeystorePassword()
    {
        return _keystorePassword;
    }
}
