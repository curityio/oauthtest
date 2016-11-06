package se.curity.oauth.core.state;

import se.curity.oauth.core.util.event.Event;

/**
 * The state of the SSL settings.
 * <p>
 * The SSL settings are used to configure the HTTP client.
 */
public class SslState implements Event
{
    private final boolean _ignoreSSL;
    private final String _trustStoreFile;
    private final String _trustStorePassword;
    private final String _keystoreFile;
    private final String _keystorePassword;

    public SslState(boolean ignoreSSL, String trustStoreFile,
                    String trustStorePassword, String keystoreFile,
                    String keystorePassword)
    {
        _ignoreSSL = ignoreSSL;
        _trustStoreFile = trustStoreFile;
        _trustStorePassword = trustStorePassword;
        _keystoreFile = keystoreFile;
        _keystorePassword = keystorePassword;
    }

    public boolean isIgnoreSSL()
    {
        return _ignoreSSL;
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
