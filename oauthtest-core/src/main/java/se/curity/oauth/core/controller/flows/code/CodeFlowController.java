package se.curity.oauth.core.controller.flows.code;

import com.sun.jersey.api.client.ClientResponse;
import javafx.beans.InvalidationListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import se.curity.oauth.core.component.Arrows;
import se.curity.oauth.core.request.CodeFlowAuthorizeRequest;
import se.curity.oauth.core.request.HttpRequest;
import se.curity.oauth.core.request.HttpResponseEvent;
import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.ListUtils;
import se.curity.oauth.core.util.event.EventBus;
import se.curity.oauth.core.util.event.Notification;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

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

    private final EventBus eventBus;

    @Nullable
    private OAuthServerState serverState = null;

    public CodeFlowController(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    @FXML
    protected void initialize()
    {
        eventBus.subscribe(OAuthServerState.class, (@Nonnull OAuthServerState serverState) ->
        {
            CodeFlowController.this.serverState = serverState;
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
            curlCommand.setText(request.toCurl());
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
        if (codeFlowAuthzState.isValid() && serverState != null && serverState.isValid())
        {
            return new CodeFlowAuthorizeRequest(serverState, codeFlowAuthzState);
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
            if (serverState != null && serverState.isValid())
            {
                return new CodeFlowAuthorizeRequest(serverState, authzState);
            }
            else
            {
                String error = (serverState == null) ?
                        "OAuth server settings are not available" :
                        "OAuth server settings have errors:" +
                                ListUtils.joinStringsWith("\n* ", serverState.getValidationErrors());

                eventBus.publish(new Notification(Notification.Level.ERROR, error));
                throw new IllegalStateException(error);
            }
        }
        else
        {
            List<String> validationErrors = authzState.getValidationErrors();
            String error = "Code Flow Authorization request has errors:" +
                    ListUtils.joinStringsWith("\n* ", validationErrors);
            eventBus.publish(new Notification(Notification.Level.ERROR, error));
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
    private String onResponse(HttpRequest request, ClientResponse response)
    {
        // always publish the response anyway
        eventBus.publish(new HttpResponseEvent(response));

        if (request instanceof CodeFlowAuthorizeRequest)
        {
            return checkAuthorizeRequestResponse((CodeFlowAuthorizeRequest) request, response);
        }
        // TODO if (request instanceof CodeFlowTokenRequest)

        throw new IllegalStateException("HttpRequest type is not known: " + request);
    }

    @Nullable
    private String checkAuthorizeRequestResponse(CodeFlowAuthorizeRequest request, ClientResponse response)
    {
        if (response.getStatus() != 302)
        {
            return String.format(
                    "The OAuth server was expected to return a 302 status code for the code flow authorize request," +
                            " but it returned %d instead. Check the server response to see what went wrong.",
                    response.getStatus());
        }

        List<String> locationHeaders = response.getHeaders().get("Location");

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
            redirectUri = new URI(locationHeaders.get(0));
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
        //Platform.runLater(() -> eventBus.publish());

        return null;
    }

    private class RequestService extends Service<ClientResponse>
    {

        @Override
        protected Task<ClientResponse> createTask()
        {
            final HttpRequest request = createRequest();
            return new Task<ClientResponse>()
            {
                @Override
                protected ClientResponse call() throws Exception
                {
                    ClientResponse response = request.send();
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
