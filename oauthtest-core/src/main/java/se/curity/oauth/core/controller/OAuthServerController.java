package se.curity.oauth.core.controller;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.PreferencesUtils;
import se.curity.oauth.core.util.Validators;
import se.curity.oauth.core.util.event.EventBus;

import java.util.List;

public class OAuthServerController
{

    @FXML
    private TextField baseUrl;

    @FXML
    private TextField authorizeEndpoint;

    @FXML
    private TextField tokenEndpoint;

    private final EventBus _eventBus;
    private final PreferencesUtils _preferencesUtils;

    public OAuthServerController(EventBus eventBus,
                                 Stage primaryStage,
                                 PreferencesUtils preferencesUtils)
    {
        _eventBus = eventBus;
        _preferencesUtils = preferencesUtils;

        primaryStage.setOnCloseRequest(e ->
                _preferencesUtils.putOAuthServerPreferences(getOAuthServerState()));
    }

    @FXML
    protected void initialize()
    {
        Validators.makeValidatableField(baseUrl, Validators::isValidUrl,
                "The OAuth server baseURL is not a valid URL");

        // set the server's initial state and publish that information
        {
            OAuthServerState initialOAuthServerState = _preferencesUtils.getOAuthServerPreferences();

            baseUrl.setText(initialOAuthServerState.getBaseUrl());
            tokenEndpoint.setText(initialOAuthServerState.getAuthorizeEndpoint());
            authorizeEndpoint.setText(initialOAuthServerState.getTokenEndpoint());

            _eventBus.publish(initialOAuthServerState);
        }

        InvalidationListener invalidationListener = observable ->
                _eventBus.publish(getOAuthServerState());

        baseUrl.textProperty().addListener(invalidationListener);
        authorizeEndpoint.textProperty().addListener(invalidationListener);
        tokenEndpoint.textProperty().addListener(invalidationListener);
    }

    private OAuthServerState getOAuthServerState()
    {
        List<String> errors = Validators.validateFields(baseUrl);

        if (errors.isEmpty())
        {
            return OAuthServerState.validState(
                    baseUrl.getText(),
                    authorizeEndpoint.getText(),
                    tokenEndpoint.getText());
        }
        else
        {
            return OAuthServerState.invalid(errors);
        }
    }

}
