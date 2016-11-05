package se.curity.oauth.core.controller;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.Validators;
import se.curity.oauth.core.util.event.EventBus;

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

        Validators.makeValidatableField( baseUrl, Validators::isValidUrl );
    }

    private OAuthServerState getOAuthServerState() {
        if ( Validators.isValidUrl( baseUrl.getText() ) ) {
            return OAuthServerState.validState(
                    baseUrl.getText(),
                    authorizeEndpoint.getText(),
                    tokenEndpoint.getText() );
        } else {
            return OAuthServerState.invalid();
        }
    }

}
