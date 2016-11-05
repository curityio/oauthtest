package se.curity.oauth.core.controller;

import javafx.stage.Stage;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.util.PreferencesUtils;
import se.curity.oauth.core.util.event.EventBus;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class OAuthServerController {

    private final OAuthServerState initialOAuthServerState;

    @FXML
    private TextField baseUrl;
    @FXML
    private TextField authorizeEndpoint;
    @FXML
    private TextField tokenEndpoint;

    private final EventBus eventBus;

    public OAuthServerController( EventBus eventBus, OAuthServerState oauthServerState, Stage primaryStage ) {

        this.eventBus = eventBus;
        this.initialOAuthServerState = oauthServerState;

        primaryStage.setOnCloseRequest( e -> PreferencesUtils.putOAuthServerPreferences(
                baseUrl.getText(), authorizeEndpoint.getText(), tokenEndpoint.getText()
        ));
    }

    @FXML
    protected void initialize() {
        baseUrl.setText( initialOAuthServerState.getBaseUrl()) ;
        tokenEndpoint.setText( initialOAuthServerState.getAuthorizeEndpoint() );
        authorizeEndpoint.setText( initialOAuthServerState.getTokenEndpoint() );

        InvalidationListener invalidationListener = observable ->
                eventBus.publish( initialOAuthServerState );

        baseUrl.textProperty().addListener( invalidationListener );
        authorizeEndpoint.textProperty().addListener( invalidationListener );
        tokenEndpoint.textProperty().addListener( invalidationListener );
    }

}
