package se.curity.oauth.core.controller;

import com.sun.jersey.api.client.ClientResponse;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import se.curity.oauth.core.component.Arrows;
import se.curity.oauth.core.controller.view.CodeFlowAuthzRequestView;
import se.curity.oauth.core.request.CodeFlowAuthorizeRequest;
import se.curity.oauth.core.request.HttpRequest;
import se.curity.oauth.core.request.HttpResponseEvent;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.event.EventBus;
import se.curity.oauth.core.util.event.Notification;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Implementation of the OAuth code flow logic.
 */
public class CodeFlowController {

    @FXML
    private CodeFlowAuthzRequestView authzRequestViewController = null;
    @FXML
    private Arrows arrowsController = null;
    @FXML
    private TextArea curlCommand = null;

    private final EventBus eventBus;

    @Nullable
    private OAuthServerState serverState = null;

    public CodeFlowController( EventBus eventBus ) {
        this.eventBus = eventBus;
    }

    @FXML
    protected void initialize() {
        eventBus.subscribe( OAuthServerState.class, ( @Nonnull OAuthServerState serverState ) -> {
            CodeFlowController.this.serverState = serverState;
            updateCurlText();
        } );

        authzRequestViewController.setInvalidationListener( observable -> updateCurlText() );

        // notice that the requestService will run whatever currentRequest is selected
        RequestService requestService = new RequestService();

        arrowsController.setSteps( Arrays.asList(
                Arrows.Step.create( "Step 1 - Authorization Request", requestService ),
                Arrows.Step.create( "Step 2 - Access Token Request", requestService )
        ) );
    }

    private void updateCurlText() {
        @Nullable HttpRequest request = createRequestIfPossible();
        if ( request != null ) {
            curlCommand.setText( request.toCurl() );
        }
    }

    @Nullable
    private HttpRequest createRequestIfPossible() {
        if ( serverState != null ) {
            return new CodeFlowAuthorizeRequest( serverState,
                    authzRequestViewController.getCodeFlowAuthzState() );
        } else {
            return null;
        }
    }

    private HttpRequest createRequest() {
        @Nullable HttpRequest request = createRequestIfPossible();
        if ( request != null ) {
            return request;
        } else {
            String error = "CodeFlow RequestService: cannot run request as it is null";
            eventBus.publish( new Notification( Notification.Level.ERROR, error ) );
            throw new IllegalStateException( error );
        }
    }

    private void onResponse( ClientResponse response ) {
        eventBus.publish( new HttpResponseEvent( response ) );
        Platform.runLater( () -> {
            // TODO set the parameters that get calculated
        } );
    }

    private class RequestService extends Service<ClientResponse> {

        @Override
        protected Task<ClientResponse> createTask() {
            final HttpRequest request = createRequest();
            return new Task<ClientResponse>() {
                @Override
                protected ClientResponse call() throws Exception {
                    ClientResponse response = request.send();
                    onResponse( response );
                    return response;
                }
            };
        }
    }

    public interface CodeFlowRequestView {
        void setInvalidationListener( InvalidationListener listener );
    }

}
