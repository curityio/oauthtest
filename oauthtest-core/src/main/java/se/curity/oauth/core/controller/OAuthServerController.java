package se.curity.oauth.core.controller;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.UserPreferences;
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
    private final UserPreferences _userPreferences;

    public OAuthServerController(EventBus eventBus,
                                 Stage primaryStage,
                                 UserPreferences userPreferences)
    {
        _eventBus = eventBus;
        _userPreferences = userPreferences;

        primaryStage.setOnCloseRequest(e ->
                _userPreferences.putOAuthServerPreferences(getOAuthServerState()));
    }

    @FXML
    protected void initialize()
    {
        Validators.makeValidatableField(baseUrl, Validators::isValidUrl,
                "The OAuth server baseURL is not a valid URL");

        // set the server's initial state and publish that information
        {
            OAuthServerState initialOAuthServerState = _userPreferences.getOAuthServerPreferences();

            baseUrl.setText(initialOAuthServerState.getBaseUrl());
            authorizeEndpoint.setText(initialOAuthServerState.getAuthorizeEndpoint());
            tokenEndpoint.setText(initialOAuthServerState.getTokenEndpoint());

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
