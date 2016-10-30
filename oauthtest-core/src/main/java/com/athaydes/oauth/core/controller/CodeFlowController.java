package com.athaydes.oauth.core.controller;

import com.athaydes.oauth.core.component.Arrows;
import com.athaydes.oauth.core.request.CodeFlowAuthorizeRequest;
import com.athaydes.oauth.core.request.HttpRequest;
import com.athaydes.oauth.core.state.CodeFlowAuthzState;
import com.athaydes.oauth.core.state.OAuthServerState;
import com.athaydes.oauth.core.util.event.EventBus;
import com.sun.jersey.api.client.ClientResponse;
import javafx.beans.InvalidationListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Implementation of the OAuth code flow logic.
 */
public class CodeFlowController {

    @FXML
    private TextField responseTypeField;
    @FXML
    private TextField clientIdField;
    @FXML
    private TextField redirectUriField;
    @FXML
    private TextField scopeField;
    @FXML
    private TextField stateField;

    @FXML
    private Node arrows;
    @FXML
    private Arrows arrowsController;

    private final EventBus eventBus;

    @Nullable
    private OAuthServerState serverState = null;
    @Nullable
    private HttpRequest currentRequest = null;

    public CodeFlowController( EventBus eventBus ) {
        this.eventBus = eventBus;
    }

    @FXML
    protected void initialize() {
        eventBus.subscribe( OAuthServerState.class, ( @Nonnull OAuthServerState serverState ) ->
                CodeFlowController.this.serverState = serverState );

        InvalidationListener authzFieldChangeListener = ( event ) -> {
            if ( serverState != null ) {
                currentRequest = new CodeFlowAuthorizeRequest( serverState, getCodeFlowAuthzState() );
                eventBus.publish( currentRequest );
            }
        };

        clientIdField.onActionProperty().addListener( authzFieldChangeListener );
        redirectUriField.onActionProperty().addListener( authzFieldChangeListener );
        scopeField.onActionProperty().addListener( authzFieldChangeListener );
        stateField.onActionProperty().addListener( authzFieldChangeListener );

        RequestService requestService = new RequestService();

        arrowsController.setSteps( Arrays.asList(
                Arrows.Step.create( "Step 1 - Authorization Request", requestService ),
                Arrows.Step.create( "Step 2 - Access Token Request", requestService )
        ) );
    }

    private CodeFlowAuthzState getCodeFlowAuthzState() {
        return new CodeFlowAuthzState(
                responseTypeField.getText(),
                clientIdField.getText(),
                redirectUriField.getText(),
                scopeField.getText(),
                stateField.getText()
        );
    }

    private class RequestService extends Service<ClientResponse> {

        @Override
        protected Task<ClientResponse> createTask() {
            final HttpRequest request = currentRequest;
            if ( request != null ) {
                return new Task<ClientResponse>() {
                    @Override
                    protected ClientResponse call() throws Exception {
                        return request.send();
                    }
                };
            } else {
                throw new IllegalStateException( "No request seems to be currently selected" );
            }
        }
    }

}
