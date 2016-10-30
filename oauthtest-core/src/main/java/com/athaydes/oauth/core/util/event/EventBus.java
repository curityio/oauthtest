package com.athaydes.oauth.core.util.event;

import com.athaydes.oauth.core.util.ListUtils;
import javafx.application.Platform;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Simple EventBus that allows Subscribers to subscribe to events by type.
 * Only subscribers to the exact type of an event are notified of an event.
 * <p>
 * All Subscribers are called on the JavaFX Thread.
 */
public class EventBus {

    private Map<Class<? extends Event>, List<Subscriber<? extends Event>>> subscriberMap =
            new HashMap<>();

    public <E extends Event> void subscribe( Class<E> eventType,
                                             Subscriber<E> subscriber ) {
        Platform.runLater( () -> subscriberMap.merge(
                eventType,
                Collections.singletonList( subscriber ),
                ListUtils::append ) );
    }

    @SuppressWarnings( "unchecked" )
    public void publish( Event event ) {
        Platform.runLater( () -> {
            subscriberMap.getOrDefault( event, Collections.emptyList() )
                    .forEach( ( Subscriber subscriber ) -> {
                        subscriber.handle( event );
                    } );
        } );
    }

}
