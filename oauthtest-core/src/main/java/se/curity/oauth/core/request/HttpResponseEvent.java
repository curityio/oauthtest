package se.curity.oauth.core.request;

import com.sun.jersey.api.client.ClientResponse;
import se.curity.oauth.core.util.event.Event;

/**
 * Simple ClientResponse Wrapper class that allows responses to be published on the EventBus.
 */
public class HttpResponseEvent implements Event
{

    private final ClientResponse response;

    public HttpResponseEvent(ClientResponse response)
    {
        this.response = response;
    }

    public ClientResponse getResponse()
    {
        return response;
    }
}
