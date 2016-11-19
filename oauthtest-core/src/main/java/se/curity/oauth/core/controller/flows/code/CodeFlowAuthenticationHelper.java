package se.curity.oauth.core.controller.flows.code;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import se.curity.oauth.core.component.Browser;
import se.curity.oauth.core.util.Promise;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Uses the {@link Browser} component to authenticate the user during the Code Flow.
 */
class CodeFlowAuthenticationHelper
{

    private final Browser _browser;
    private String _authenticationDoneUrl;

    CodeFlowAuthenticationHelper(Browser browser)
    {
        _browser = browser;
        _authenticationDoneUrl = getClass().getResource("/html/browser/authentication-done.html").toExternalForm();
    }

    Promise<URI, Void> authenticate(URI uri, Window ownerWindow, String redirectUri)
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

            _browser.initializeWith("Please authenticate to proceed.",
                    uri,
                    (browse, loadedUri) -> reactIfAuthenticationDone(browse, loadedUri, redirectUri, () ->
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

            if (_browser.getScene() != null)
            {
                // force the Browser to become scene-less
                _browser.getScene().setRoot(new Group());
            }

            Scene dialogScene = new Scene(_browser, 600, 600);
            dialog.setScene(dialogScene);
            dialog.show();

            System.out.println("CodeFlowAuthenticationHelper: User being redirected to: " + uri);
        });

        return deferredAuthentication.getPromise();
    }

    private void reactIfAuthenticationDone(Browser browser,
                                           URI uri,
                                           String redirectUri,
                                           Runnable onAuthenticationDone)
    {
        WebEngine engine = browser.getWebEngine();
        String uriAddress = asSimpleAddress(uri);

        if (uriAddress.equals(redirectUri))
        {
            Platform.runLater(() ->
            {
                // authentication done!
                engine.load(_authenticationDoneUrl);

                browser.getBackButton().setDisable(true);
                browser.getNextButton().setDisable(true);
                onAuthenticationDone.run();
            });
        }
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
