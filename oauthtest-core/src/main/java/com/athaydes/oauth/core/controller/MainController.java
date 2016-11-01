package com.athaydes.oauth.core.controller;

import com.athaydes.oauth.core.request.HttpResponseEvent;
import com.athaydes.oauth.core.util.event.EventBus;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class MainController {

    @FXML
    private TextArea currentResponse;
    @FXML
    private Node codeFlow;
    @FXML
    private CodeFlowController codeFlowController;

    private final EventBus eventBus;

    public MainController( EventBus eventBus ) {
        this.eventBus = eventBus;
    }

    @FXML
    protected void initialize() {
        eventBus.subscribe( HttpResponseEvent.class, responseEvent -> {
            System.out.println("RESPONSE: " + responseEvent.getResponse());
            String responseText = responseEvent.getResponse().getEntity( String.class );
            currentResponse.setText( responseText );
        } );
    }

}
