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

import java.net.URI;

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

    void authenticate(URI uri, Window ownerWindow, OAuthServerState oauthServerState)
    {
        Platform.runLater(() ->
        {
            WebView webView = new WebView();
            webView.getEngine().load(uri.toString());

            Stage dialog = new Stage();
            dialog.setTitle("User Authentication");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(ownerWindow);

            Browser browser = _browserFactory.create("Please authenticate to proceed.",
                    uri,
                    (browse, loadedUri) -> reactIfAuthenticationDone(browse, loadedUri, oauthServerState));

            Scene dialogScene = new Scene(browser, 600, 600);
            dialog.setScene(dialogScene);
            dialog.show();
            System.out.println("USER REDIRECTED TO " + uri);
        });
    }

    private void reactIfAuthenticationDone(Browser browser, URI uri, OAuthServerState oauthServerState)
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
                System.out.println("Hey, we are being redirected back to " + uriAddress);

                browser.getBackButton().setDisable(true);
                browser.getNextButton().setDisable(true);

                engine.load(getClass().getResource("/html/browser/authentication-done.html").toExternalForm());
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
