package se.curity.oauth.core.request;

import se.curity.oauth.core.util.event.Event;

import javax.ws.rs.core.Response;

/**
 * Simple ClientResponse Wrapper class that allows responses to be published on the EventBus.
 */
public class HttpResponseEvent implements Event
{

    private final Response _response;

    public HttpResponseEvent(Response response)
    {
        this._response = response;
    }

    public Response getResponse()
    {
        return _response;
    }
}
