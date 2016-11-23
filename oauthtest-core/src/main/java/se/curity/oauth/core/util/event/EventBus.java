package se.curity.oauth.core.util.event;

import javafx.application.Platform;
import se.curity.oauth.core.util.ListUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Simple EventBus that allows Subscribers to subscribe to events by type.
 * Only subscribers to the exact type of an event are notified of an event.
 * <p>
 * Subscribers always get notified immediately of the latest event of the type they subscribe to upon subscription.
 * <p>
 * All Subscribers are called on the JavaFX Thread.
 */
public class EventBus
{

    private final Map<Class<? extends Event>, List<Subscriber<? extends Event>>> _subscriberMap =
            new HashMap<>();

    private final Map<Class<? extends Event>, Event> _latestEventByType = new HashMap<>();

    public <E extends Event> void subscribe(Class<E> eventType,
                                            Subscriber<E> subscriber)
    {
        Platform.runLater(() ->
        {
            _subscriberMap.merge(
                    eventType,
                    Collections.singletonList(subscriber),
                    ListUtils::append);

            @Nullable Event latestEvent = _latestEventByType.get(eventType);

            if (latestEvent != null)
            {
                handle(subscriber, latestEvent);
            }
        });
    }

    public void publish(Event event)
    {
        Platform.runLater(() ->
        {
            List<Subscriber<? extends Event>> subscribers =
                    _subscriberMap.getOrDefault(event.getClass(), Collections.emptyList());

            for (Subscriber subscriber : subscribers)
            {
                handle(subscriber, event);
            }

            _latestEventByType.put(event.getClass(), event);
        });
    }

    @SuppressWarnings("unchecked")
    private static void handle(Subscriber subscriber, Event event)
    {
        try
        {
            subscriber.handle(event);
        }
        catch (Exception e)
        {
            System.err.println("Exception thrown by subscriber: " + subscriber);
            e.printStackTrace();
        }
    }

}
