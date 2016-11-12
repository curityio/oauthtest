package se.curity.oauth.core.controller.flows.code;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import se.curity.oauth.core.component.Browser;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.Promise;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Uses the {@link Browser} component to authenticate the user during the Code Flow.
 */
class CodeFlowAuthenticationHelper
{

    private final Browser.Factory _browserFactory;

    CodeFlowAuthenticationHelper(Browser.Factory browserFactory)
    {
        _browserFactory = browserFactory;
    }

    Promise<URI, Void> authenticate(URI uri, Window ownerWindow, OAuthServerState oauthServerState)
    {
        Promise.Deferred<URI, Void> deferredAuthentication = new Promise.Deferred<>();

        Platform.runLater(() ->
        {
            WebView webView = new WebView();
            webView.getEngine().load(uri.toString());

            Stage dialog = new Stage();
            dialog.setTitle("User Authentication");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(ownerWindow);

            AtomicBoolean success = new AtomicBoolean(false);

            Browser browser = _browserFactory.create("Please authenticate to proceed.",
                    uri,
                    (browse, loadedUri) -> reactIfAuthenticationDone(browse, loadedUri, oauthServerState, () ->
                    {
                        success.set(true);
                        deferredAuthentication.fullfill(loadedUri);
                    }));

            dialog.setOnHiding(event ->
            {
                if (!success.get())
                {
                    //noinspection ConstantConditions (Void is always null)
                    deferredAuthentication.fail(null);
                }
            });

            Scene dialogScene = new Scene(browser, 600, 600);
            dialog.setScene(dialogScene);
            dialog.show();
            System.out.println("USER REDIRECTED TO " + uri);
        });

        return deferredAuthentication.getPromise();
    }

    private void reactIfAuthenticationDone(Browser browser,
                                           URI uri,
                                           OAuthServerState oauthServerState,
                                           Runnable onAuthenticationDone)
    {
        WebEngine engine = browser.getWebEngine();
        Platform.runLater(() ->
        {
            String oauthServerBaseUrl = oauthServerState.getBaseUrl();
            String authorizeEndpoint = oauthServerState.getAuthorizeEndpoint();

            String uriAddress = asSimpleAddress(uri);
            String oauthServerAddress = asSimpleAddress(oauthServerBaseUrl, authorizeEndpoint);

            if (uriAddress.equals(oauthServerAddress))
            {
                browser.getBackButton().setDisable(true);
                browser.getNextButton().setDisable(true);

                engine.load(getClass().getResource("/html/browser/authentication-done.html").toExternalForm());

                onAuthenticationDone.run();
            }
        });
    }

    private static String asSimpleAddress(String baseUrl, String path)
    {
        while (baseUrl.endsWith("/"))
        {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        if (!path.startsWith("/"))
        {
            path = "/" + path;
        }

        return baseUrl + path;
    }

    private static String asSimpleAddress(URI uri)
    {
        String scheme = (uri.getScheme() == null ? "http://" : uri.getScheme() + "://");
        String hostName = (uri.getHost() == null ? "" : uri.getHost());
        String port = (uri.getPort() > 0 ? ":" + Integer.toString(uri.getPort()) : "");
        String path = (uri.getPath() == null ? "" : uri.getPath());

        return scheme + hostName + port + path;
    }


}
