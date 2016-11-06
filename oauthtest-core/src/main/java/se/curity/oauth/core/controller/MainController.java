package se.curity.oauth.core.controller;

import com.sun.jersey.api.client.ClientResponse;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import se.curity.oauth.core.controller.flows.code.CodeFlowController;
import se.curity.oauth.core.request.HttpResponseEvent;
import se.curity.oauth.core.util.event.EventBus;

public class MainController
{

    @FXML
    private TextArea currentResponse;
    @FXML
    private Node codeFlow;
    @FXML
    private CodeFlowController codeFlowController;

    private final EventBus eventBus;

    public MainController(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    @FXML
    protected void initialize()
    {
        eventBus.subscribe(HttpResponseEvent.class, responseEvent ->
        {
            ClientResponse response = responseEvent.getResponse();

            System.out.println("RESPONSE: " + responseEvent.getResponse());
            String responseText = responseEvent.getResponse().getEntity(String.class);
            String statusLine = "STATUS: " + response.getStatus() + " - " + response.getStatusInfo().getReasonPhrase();
            String headers = "HEADERS: " + response.getHeaders().toString();

            currentResponse.setText(statusLine + "\n" + headers + "\n" + responseText);
        });
    }

}
