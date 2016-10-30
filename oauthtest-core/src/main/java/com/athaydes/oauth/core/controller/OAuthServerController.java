package com.athaydes.oauth.core.controller;

import com.athaydes.oauth.core.state.OAuthServerState;
import com.athaydes.oauth.core.util.event.EventBus;
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

        baseUrl.onActionProperty().addListener( invalidationListener );
        authorizeEndpoint.onActionProperty().addListener( invalidationListener );
        tokenEndpoint.onActionProperty().addListener( invalidationListener );
    }

    private OAuthServerState getOAuthServerState() {
        return new OAuthServerState(
                baseUrl.getText(),
                authorizeEndpoint.getText(),
                tokenEndpoint.getText() );
    }

}
