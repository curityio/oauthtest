package com.athaydes.oauth.core.controller;

import com.athaydes.oauth.core.component.Arrows;
import com.athaydes.oauth.core.controller.view.CodeFlowAuthzRequestView;
import com.athaydes.oauth.core.request.CodeFlowAuthorizeRequest;
import com.athaydes.oauth.core.request.HttpRequest;
import com.athaydes.oauth.core.request.HttpResponseEvent;
import com.athaydes.oauth.core.state.OAuthServerState;
import com.athaydes.oauth.core.util.event.EventBus;
import com.athaydes.oauth.core.util.event.Notification;
import com.sun.jersey.api.client.ClientResponse;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Implementation of the OAuth code flow logic.
 */
public class CodeFlowController {

    @FXML
    private Node authzRequestView;
    @FXML
    private CodeFlowAuthzRequestView authzRequestViewController;

    @FXML
    private Node arrows;
    @FXML
    private Arrows arrowsController;

    private final EventBus eventBus;

    @Nullable
    private OAuthServerState serverState = null;
    @Nullable
    private volatile HttpRequest currentRequest = null;

    public CodeFlowController( EventBus eventBus ) {
        this.eventBus = eventBus;
    }

    @FXML
    protected void initialize() {
        eventBus.subscribe( OAuthServerState.class, ( @Nonnull OAuthServerState serverState ) ->
                CodeFlowController.this.serverState = serverState );

        InvalidationListener authzFieldChangeListener = ( event ) -> {
            if ( serverState != null ) {
                System.out.println( "SETTING CURRENT REQ" );
                currentRequest = new CodeFlowAuthorizeRequest( serverState,
                        authzRequestViewController.getCodeFlowAuthzState() );
                System.out.println( "Current request set to : " + currentRequest );
            } else {
                System.out.println("SERVER STATE IS NULL");
            }
        };

        authzRequestViewController.setInvalidationListener( authzFieldChangeListener );

        // notice that the requestService will run whatever currentRequest is selected
        RequestService requestService = new RequestService();

        arrowsController.setSteps( Arrays.asList(
                Arrows.Step.create( "Step 1 - Authorization Request", requestService ),
                Arrows.Step.create( "Step 2 - Access Token Request", requestService )
        ) );
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
            final HttpRequest request = currentRequest;
            if ( request != null ) {
                return new Task<ClientResponse>() {
                    @Override
                    protected ClientResponse call() throws Exception {
                        ClientResponse response = request.send();
                        onResponse( response );
                        return response;
                    }
                };
            } else {
                String error = "CodeFlow RequestService: cannot run request as it is null";
                eventBus.publish( new Notification( Notification.Level.ERROR, error ) );
                throw new IllegalStateException( error );
            }
        }
    }

}
