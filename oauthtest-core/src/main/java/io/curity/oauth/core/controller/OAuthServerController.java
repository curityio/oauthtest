package io.curity.oauth.core.controller;

import io.curity.oauth.core.state.OAuthServerState;
import io.curity.oauth.core.util.event.EventBus;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class OAuthServerController {

    @FXML
    private TextField baseUrl;
    @FXML
    private TextField authorizeEndpoint;
    @FXML
    private TextField tokenEndpoint;

    private final EventBus eventBus;

    public OAuthServerController( EventBus eventBus ) {
        this.eventBus = eventBus;
    }

    @FXML
    protected void initialize() {
        InvalidationListener invalidationListener = observable ->
                eventBus.publish( getOAuthServerState() );

        baseUrl.textProperty().addListener( invalidationListener );
        authorizeEndpoint.textProperty().addListener( invalidationListener );
        tokenEndpoint.textProperty().addListener( invalidationListener );
    }

    private OAuthServerState getOAuthServerState() {
        return new OAuthServerState(
                baseUrl.getText(),
                authorizeEndpoint.getText(),
                tokenEndpoint.getText() );
    }

}
