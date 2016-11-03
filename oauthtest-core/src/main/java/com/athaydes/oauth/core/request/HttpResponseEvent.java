package com.athaydes.oauth.core.request;

import com.athaydes.oauth.core.util.event.Event;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Simple ClientResponse Wrapper class that allows responses to be published on the EventBus.
 */
public class HttpResponseEvent implements Event {

    private final ClientResponse response;

    public HttpResponseEvent( ClientResponse response ) {
        this.response = response;
    }

    public ClientResponse getResponse() {
        return response;
    }
}