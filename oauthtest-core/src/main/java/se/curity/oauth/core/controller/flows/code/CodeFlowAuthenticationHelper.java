package se.curity.oauth.core.controller.flows.code;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import se.curity.oauth.core.component.Browser;
import se.curity.oauth.core.request.CodeFlowAuthorizeRequest;
import se.curity.oauth.core.request.HttpRequest;
import se.curity.oauth.core.util.Promise;

import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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

    Promise<URI, Void> authenticate(CodeFlowAuthorizeRequest request, URI uri, Window ownerWindow)
    {
        Promise.Deferred<URI, Void> deferredAuthentication = new Promise.Deferred<>();
        String redirectUri = request.getState().getRedirectUri().trim();

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
                    uri, onUriLoadedByBrowser(deferredAuthentication, redirectUri, success));

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

    private Consumer<URI> onUriLoadedByBrowser(Promise.Deferred<URI, Void> deferredAuthentication,
                                               String redirectUri, AtomicBoolean success)
    {
        // this will run when authentication has been performed
        Consumer<URI> onAuthnDone = (loadedUri) ->
        {
            _browser.getWebEngine().load(_authenticationDoneUrl);

            _browser.getBackButton().setDisable(true);
            _browser.getNextButton().setDisable(true);

            success.set(true);
            deferredAuthentication.fullfill(loadedUri);
        };

        if (redirectUri.isEmpty())
        {
            return (loadedUri) ->
            {
                if (isAuthenticationDone(loadedUri))
                {
                    onAuthnDone.accept(loadedUri);
                }
            };
        }
        else
        {
            return (loadedUri) ->
            {
                if (isAuthenticationDone(redirectUri, loadedUri))
                {
                    onAuthnDone.accept(loadedUri);
                }
            };
        }
    }

    private boolean isAuthenticationDone(URI uri)
    {
        MultivaluedMap<String, String> queryParameters = HttpRequest.parseQueryParameters(uri.getQuery());

        // we know we should run a token request when the Authorization server gives us the code
        return queryParameters.containsKey("code");
    }

    private boolean isAuthenticationDone(String redirectUri,
                                         URI uri)
    {
        // we know we should run a token request when the Authorization server redirects to our redirect_uri
        String uriAddress = asSimpleAddress(uri);
        return uriAddress.equals(redirectUri);
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
