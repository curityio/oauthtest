package com.athaydes.oauth.core.util.event;

import com.athaydes.oauth.core.util.ListUtils;
import javafx.application.Platform;

import javax.annotation.Nullable;
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

    private final Map<Class<? extends Event>, List<Subscriber<? extends Event>>> subscriberMap =
            new HashMap<>();

    private final Map<Class<? extends Event>, Event> unhandledEvents = new HashMap<>();

    public <E extends Event> void subscribe( Class<E> eventType,
                                             Subscriber<E> subscriber ) {
        Platform.runLater( () -> {
            subscriberMap.merge(
                    eventType,
                    Collections.singletonList( subscriber ),
                    ListUtils::append );
            @Nullable Event unhandledEvent = unhandledEvents.get( eventType );
            if ( unhandledEvent != null ) {
                handle( subscriber, unhandledEvent );
            }
        } );
    }

    public void publish( Event event ) {
        Platform.runLater( () -> {
            List<Subscriber<? extends Event>> subscribers =
                    subscriberMap.getOrDefault( event.getClass(), Collections.emptyList() );

            if ( subscribers.isEmpty() ) {
                unhandledEvents.put( event.getClass(), event );
            } else for (Subscriber subscriber : subscribers) {
                handle( subscriber, event );
            }
        } );
    }

    @SuppressWarnings( "unchecked" )
    private static void handle( Subscriber subscriber, Event event ) {
        subscriber.handle( event );
    }

}
