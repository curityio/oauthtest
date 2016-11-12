package se.curity.oauth.core.util;

import javafx.application.Platform;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The Application-level CookieManager wrapper which can be used by parts of the application that must check
 * cookies in all Java HTTP requests/responses.
 */
public enum ObservableCookieManager
{
    INSTANCE;

    private final List<Consumer<HttpCookie>> _cookieListeners = new ArrayList<>();

    public void addCookieListener(Consumer<HttpCookie> listener)
    {
        Platform.runLater(() -> _cookieListeners.add(listener));
    }

    ObservableCookieManager()
    {
        CookieManager.setDefault(new CookieManager()
        {
            @Override
            public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException
            {
                Set<HttpCookie> beforeCookies = new HashSet<>(getCookieStore().get(uri));

                super.put(uri, responseHeaders);

                List<HttpCookie> afterCookies = getCookieStore().get(uri);

                Platform.runLater(() -> afterCookies.stream()
                        .filter(it -> !beforeCookies.contains(it))
                        .forEach(it -> _cookieListeners.forEach(listener -> listener.accept(it)))
                );
            }
        });
    }


}
