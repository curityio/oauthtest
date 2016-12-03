package se.curity.oauth.core.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import se.curity.oauth.core.controller.flows.code.CodeFlowController;
import se.curity.oauth.core.request.HttpResponseEvent;
import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.state.GeneralState;
import se.curity.oauth.core.state.OAuthServerState;
import se.curity.oauth.core.state.SslState;
import se.curity.oauth.core.util.UnsafeSSLContextProvider;
import se.curity.oauth.core.util.UserPreferences;
import se.curity.oauth.core.util.event.EventBus;

import javax.ws.rs.core.Response;

public class MainController
{
    @FXML
    private TextArea currentResponse;
    @FXML
    private Node codeFlow;
    @FXML
    private CodeFlowController codeFlowController;

    private final EventBus _eventBus;
    private final UserPreferences _userPreferences;

    public MainController(EventBus eventBus, Stage primaryStage, UserPreferences userPreferences)
    {
        _eventBus = eventBus;
        _userPreferences = userPreferences;
    }

    @FXML
    protected void initialize()
    {
        _eventBus.subscribe(HttpResponseEvent.class, responseEvent ->
        {
            Response response = responseEvent.getResponse();

            System.out.println("RESPONSE: " + responseEvent.getResponse());
            String responseText = (response.hasEntity() ?
                    responseEvent.getResponse().readEntity(String.class) : "");
            String statusLine = "STATUS: " + response.getStatus() + " - " +
                    response.getStatusInfo().getReasonPhrase();
            String headers = "HEADERS: " + response.getHeaders().toString();

            currentResponse.setText(statusLine + "\n" + headers + "\n" + responseText);
        });

        // listen to SSL State so we can turn off SSL checks globally if necessary
        _eventBus.subscribe(SslState.class, sslState ->
        {
            _userPreferences.putSslPreferences(sslState); // Save in preferences

            switch (sslState.getSslOption())
            {
                case USE_KEYSTORE:
                    UnsafeSSLContextProvider.getInstance().resetSSLContext();
                    break;
                case TRUST_OAUTH_SERVER_CERTIFICATE:
                    // TODO check OAuth server certificate and trust it later
                case IGNORE_SSL:
                    UnsafeSSLContextProvider.getInstance().setUnsafeSSLContextGlobally();
            }
        });

        // Subscribe to other state changes and save changes in user's preferences
        _eventBus.subscribe(GeneralState.class, _userPreferences::putGeneralPreferences);
        _eventBus.subscribe(OAuthServerState.class, _userPreferences::putOAuthServerPreferences);
        _eventBus.subscribe(CodeFlowAuthzState.class, _userPreferences::putCodeFlowPreferences);
    }
}
