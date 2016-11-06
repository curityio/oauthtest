package se.curity.oauth.core.controller;

import javafx.stage.Stage;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.PreferencesUtils;
import se.curity.oauth.core.util.event.EventBus;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class OAuthServerController
{
    private final OAuthServerState _initialOAuthServerState;

    @FXML
    private TextField baseUrl;

    @FXML
    private TextField authorizeEndpoint;

    @FXML
    private TextField tokenEndpoint;

    private final EventBus _eventBus;

    public OAuthServerController(EventBus eventBus, OAuthServerState oauthServerState, Stage primaryStage)
    {
        _eventBus = eventBus;
        _initialOAuthServerState = oauthServerState;

        primaryStage.setOnCloseRequest(e ->
                PreferencesUtils.putOAuthServerPreferences(getOAuthServerState()));
    }

    @FXML
    protected void initialize()
    {
        baseUrl.setText(_initialOAuthServerState.getBaseUrl());
        tokenEndpoint.setText(_initialOAuthServerState.getAuthorizeEndpoint());
        authorizeEndpoint.setText(_initialOAuthServerState.getTokenEndpoint());

        InvalidationListener invalidationListener = observable ->
                _eventBus.publish(getOAuthServerState());

        baseUrl.textProperty().addListener(invalidationListener);
        authorizeEndpoint.textProperty().addListener(invalidationListener);
        tokenEndpoint.textProperty().addListener(invalidationListener);
    }

    private OAuthServerState getOAuthServerState()
    {
        return new OAuthServerState(baseUrl.getText(),
                authorizeEndpoint.getText(),
                tokenEndpoint.getText());
    }
}
