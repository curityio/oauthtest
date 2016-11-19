package se.curity.oauth.core.controller.flows.code;

import javafx.beans.InvalidationListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import se.curity.oauth.core.component.Arrows;
import se.curity.oauth.core.component.Browser;
import se.curity.oauth.core.request.CodeFlowAuthorizeRequest;
import se.curity.oauth.core.request.HttpRequest;
import se.curity.oauth.core.request.HttpResponseEvent;
import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.state.GeneralState;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.state.SslState;
import se.curity.oauth.core.util.Either;
import se.curity.oauth.core.util.ListUtils;
import se.curity.oauth.core.util.ObservableCookieManager;
import se.curity.oauth.core.util.event.EventBus;
import se.curity.oauth.core.util.event.Notification;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.CookieStore;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static se.curity.oauth.core.request.HttpRequest.parseQueryParameters;

/**
 * Implementation of the OAuth code flow logic.
 */
public class CodeFlowController
{

    @FXML
    private CodeFlowAuthzRequestController authzRequestViewController = null;
    @FXML
    private Arrows arrowsController = null;
    @FXML
    private TextArea curlCommand = null;

    private final EventBus _eventBus;
    private final CodeFlowAuthenticationHelper _authenticationHelper;

    @Nullable
    private OAuthServerState _oauthServerState = null;

    @Nullable
    private SslState _sslState = null;
    private GeneralState _generalState;

    public CodeFlowController(EventBus eventBus, Browser browser)
    {
        this._eventBus = eventBus;
        _authenticationHelper = new CodeFlowAuthenticationHelper(browser);
    }

    @FXML
    protected void initialize()
    {
        _eventBus.subscribe(OAuthServerState.class, (@Nonnull OAuthServerState serverState) ->
        {
            CodeFlowController.this._oauthServerState = serverState;
            updateCurlText();
        });

        _eventBus.subscribe(SslState.class, (@Nonnull SslState sslState) ->
        {
            _sslState = sslState;
            updateCurlText();
        });

        _eventBus.subscribe(GeneralState.class, (@Nonnull GeneralState generalState) ->
        {
            _generalState = generalState;
            updateCurlText();
        });

        authzRequestViewController.setInvalidationListener(observable -> updateCurlText());

        // notice that the requestService will run whatever currentRequest is selected
        RequestService requestService = new RequestService();

        arrowsController.setSteps(Arrays.asList(
                Arrows.Step.create("Step 1 - Authorization Request", requestService),
                Arrows.Step.create("Step 2 - Access Token Request", requestService)
        ));
    }

    private void updateCurlText()
    {
        @Nullable HttpRequest request = createRequestIfPossible();
        if (request != null)
        {
            curlCommand.setText(request.toCurl(_sslState, _generalState));
        }
        else
        {
            curlCommand.setText("");
        }
    }

    @Nullable
    private HttpRequest createRequestIfPossible()
    {
        CodeFlowAuthzState codeFlowAuthzState = authzRequestViewController.getCodeFlowAuthzState();
        if (codeFlowAuthzState.isValid() && _oauthServerState != null && _oauthServerState.isValid())
        {
            return new CodeFlowAuthorizeRequest(_oauthServerState, codeFlowAuthzState);
        }
        else
        {
            return null;
        }
    }

    private HttpRequest createRequest()
    {
        CodeFlowAuthzState authzState = authzRequestViewController.getCodeFlowAuthzState();
        if (authzState.isValid())
        {
            if (_oauthServerState != null && _oauthServerState.isValid())
            {
                return new CodeFlowAuthorizeRequest(_oauthServerState, authzState);
            }
            else
            {
                String error = (_oauthServerState == null) ?
                        "OAuth server settings are not available" :
                        "OAuth server settings have errors:" +
                                ListUtils.joinStringsWith("\n* ", _oauthServerState.getValidationErrors());

                _eventBus.publish(new Notification(Notification.Level.ERROR, error));
                throw new IllegalStateException(error);
            }
        }
        else
        {
            List<String> validationErrors = authzState.getValidationErrors();
            String error = "Code Flow Authorization request has errors:" +
                    ListUtils.joinStringsWith("\n* ", validationErrors);
            _eventBus.publish(new Notification(Notification.Level.ERROR, error));
            throw new IllegalStateException(error);
        }
    }

    /**
     * React to a response having been received. This is called from a Worker Thread.
     *
     * @param request  request for which the response was sent.
     * @param response response sent by the server
     * @return null if success, or an error message if the response is not successful.
     */
    @Nullable
    private String onResponse(HttpRequest request, Response response)
    {
        // always publish the response anyway
        _eventBus.publish(new HttpResponseEvent(response));

        if (request instanceof CodeFlowAuthorizeRequest)
        {
            return redirectUserToAuthenticate((CodeFlowAuthorizeRequest) request, response);
        }
        // TODO if (request instanceof CodeFlowTokenRequest)

        throw new IllegalStateException("HttpRequest type is not known: " + request);
    }

    @Nullable
    private String redirectUserToAuthenticate(CodeFlowAuthorizeRequest request, Response response)
    {
        Either<URI, String> redirectResult = validateRedirect(response);

        return redirectResult.onResult(uri ->
        {
            if (_oauthServerState != null)
            {
                String redirectUri = request.getState().getRedirectUri();

                _authenticationHelper.authenticate(uri, curlCommand.getScene().getWindow(), redirectUri)
                        .onSuccess((nextUri) ->
                        {
                            HttpRequest afterAuthnRequest = new CodeFlowAfterAuthenticationRequest(nextUri);
                            Response afterAuthnResponse = afterAuthnRequest.send(_sslState);

                            _eventBus.publish(new HttpResponseEvent(afterAuthnResponse));

                            @Nullable String error = checkAuthorizeRequestResponse(request, afterAuthnResponse);

                            if (error != null)
                            {
                                _eventBus.publish(new Notification(Notification.Level.ERROR, error));
                            }
                        }).onFailure((failure) -> _eventBus.publish(new Notification(Notification.Level.ERROR,
                        "Authentication was not successful. Cannot continue the OAuth Code Flow.")));
            }
            else
            {
                throw new IllegalStateException("Running browser but OAuth Server State is unknown");
            }

            //noinspection ConstantConditions (null is acceptable as a return value)
            return null;
        }, Function.identity());
    }

    private Either<URI, String> validateRedirect(Response response)
    {
        int status = response.getStatus();

        if (status < 300 || status >= 400)
        {
            return Either.failure(String.format(
                    "The OAuth server was expected to return a status code in the range of 300 to 399 for the code " +
                            "flow authorize request, but it returned %d instead. Check the server response to see what " +
                            "went wrong.", status));
        }

        List<Object> locationHeaders = response.getHeaders().get("Location");

        if (locationHeaders == null || locationHeaders.size() != 1)
        {
            String errorSuffix = (locationHeaders == null) ?
                    "failed to send the Location header, which is mandatory." :
                    "sent more than one Location header, so it is not possible to know which one to follow!";

            return Either.failure("The OAuth server returned an invalid HTTP response! Although it sent a '302' status code, it " +
                    errorSuffix);
        }

        try
        {
            return Either.success(new URI(locationHeaders.get(0).toString()));
        }
        catch (URISyntaxException e)
        {
            return Either.failure(
                    "The OAuth server tried to redirect the client to an invalid URI:\n" + e.getMessage());
        }
    }

    @Nullable
    private String checkAuthorizeRequestResponse(CodeFlowAuthorizeRequest request, Response response)
    {
        int status = response.getStatus();

        if (status < 300 || status >= 400)
        {
            return String.format(
                    "The OAuth server was expected to return a status code in the range of 300 to 399 for the code " +
                            "flow authorize request, but it returned %d instead. Check the server response to see " +
                            "what went wrong.", status);
        }

        List<Object> locationHeaders = response.getHeaders().get("Location");

        if (locationHeaders == null || locationHeaders.size() != 1)
        {
            String errorSuffix = (locationHeaders == null) ?
                    "failed to send the Location header, which is mandatory." :
                    "sent more than one Location header, so it is not possible to know which one to follow!";

            return "The OAuth server returned an invalid HTTP response! Although it sent a '302' status code, it " +
                    errorSuffix;
        }

        URI redirectUri;

        try
        {
            redirectUri = new URI(locationHeaders.get(0).toString());
        }
        catch (URISyntaxException e)
        {
            return "The OAuth server tried to redirect the client to an invalid URI:\n" + e.getMessage();
        }

        MultivaluedMap<String, String> queryParameters = parseQueryParameters(redirectUri.getQuery());
        @Nullable List<String> codeValues = queryParameters.get("code");

        if (codeValues == null || codeValues.isEmpty())
        {
            return "The OAuth server failed to return a 'code' value in the redirect URI.";
        }

        if (codeValues.size() > 1)
        {
            return "The OAuth server returned multiple 'code' values in the redirect URI, whereas only one " +
                    "was expected.";
        }

        String code = codeValues.get(0);

        if (code.isEmpty())
        {
            return "The OAuth server returned an empty 'code' in the redirect URI. Even though the OAuth " +
                    "specification does not determine the length of the code, an empty code ought to be " +
                    "considered invalid!";
        }

        @Nullable List<String> stateValues = queryParameters.get("state");
        boolean gotStateBack = (stateValues != null && !stateValues.isEmpty());

        String providedState = request.getState().getState().trim();
        @Nullable String state;

        if (providedState.isEmpty())
        {
            if (gotStateBack)
            {
                return "The OAuth server returned a 'state' in the redirect URI even though the client did not " +
                        "give it one. The server should not have returned any state in this case.";
            }

            state = null;
        }
        else // if a state was provided
        {
            if (!gotStateBack)
            {
                return "The OAuth server failed to return 'state' in the redirect URI even though the client " +
                        "provided one. The server should have returned the state in this case.";
            }

            if (stateValues.size() != 1)
            {
                return "The OAuth server returned multiple 'state' values in the redirect URI. " +
                        "The server should have returned only the state provided by the client in this case.";
            }

            state = stateValues.get(0);
        }

        System.out.println("Got a valid response for the authorize request: code=" + code + ", state=" + state);
        System.out.println("Redirect URI: " + redirectUri);

        // TODO set the Location for the browser to follow
        //Platform.runLater(() -> _eventBus.publish());

        return null;
    }

    private static void confirmRemovalOfCookies()
    {
        CookieStore cookieStore = ObservableCookieManager.INSTANCE.getCookieStore();

        if (!cookieStore.getCookies().isEmpty())
        {

            ButtonType yesButton = new ButtonType("Delete Cookies", ButtonBar.ButtonData.OK_DONE);
            ButtonType noButton = new ButtonType("Proceed", ButtonBar.ButtonData.CANCEL_CLOSE);

            String content = "When your session already contains cookies, it is possible that you have " +
                    "authenticated before because your authentication service may remember you by setting a cookie.\n" +
                    "If you go back to that service with a valid authentication cookie, the authentication service " +
                    "will just redirect you back to the OAuth server without bothering you again with credential " +
                    "checking. In normal circumstances, this is great. If, however, you want to re-test authentication, " +
                    "that's not what you want.\n\n" +
                    "To forget your cookies from a previous session and force authentication to happen again, " +
                    "choose 'Delete Cookies', otherwise, choose 'Proceed'.";

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, yesButton, noButton);

            alert.setTitle("You've got cookies!");
            alert.setHeaderText("Your current session seems to contain cookies. Do you want to remove them?");
            alert.getDialogPane().getChildren().add(new TextArea("Cookies: " + cookieStore.getCookies()));

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == yesButton)
            {
                System.out.println("Removing all cookies");
                cookieStore.removeAll();
            }
        }
    }

    private class RequestService extends Service<Response>
    {

        @Override
        protected Task<Response> createTask()
        {

            final HttpRequest request = createRequest();
            final @Nullable SslState sslState = _sslState;

            // on the first step, we should clean up the cookies from possible previous sessions
            if (request instanceof CodeFlowAuthorizeRequest)
            {
                confirmRemovalOfCookies();
            }

            return new Task<Response>()
            {
                @Override
                protected Response call() throws Exception
                {
                    Response response = request.send(sslState);
                    @Nullable String error = onResponse(request, response);

                    if (error == null)
                    {
                        return response;
                    }
                    else
                    {
                        throw new RuntimeException(error);
                    }
                }
            };
        }
    }

    public interface CodeFlowRequestController
    {
        void setInvalidationListener(InvalidationListener listener);
    }

}
