package se.curity.oauth.core.controller;

import javafx.stage.Stage;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.PreferencesUtils;
import se.curity.oauth.core.util.event.EventBus;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.Validators;
import se.curity.oauth.core.util.event.EventBus;

import java.util.List;

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
        Validators.makeValidatableField( baseUrl, Validators::isValidUrl,
                "The OAuth server baseURL is not a valid URL" );

        baseUrl.setText(_initialOAuthServerState.getBaseUrl());
        tokenEndpoint.setText(_initialOAuthServerState.getAuthorizeEndpoint());
        authorizeEndpoint.setText(_initialOAuthServerState.getTokenEndpoint());

        InvalidationListener invalidationListener = observable ->
                _eventBus.publish(getOAuthServerState());

        baseUrl.textProperty().addListener(invalidationListener);
        authorizeEndpoint.textProperty().addListener(invalidationListener);
        tokenEndpoint.textProperty().addListener(invalidationListener);
    }

    private OAuthServerState getOAuthServerState() {
        List<String> errors = Validators.validateFields( baseUrl );

        if ( errors.isEmpty() ) {
            return OAuthServerState.validState(
                    baseUrl.getText(),
                    authorizeEndpoint.getText(),
                    tokenEndpoint.getText() );
        } else {
            return OAuthServerState.invalid( errors );
        }
    }

}
