package com.athaydes.oauth.core.util.event;

/**
 * Notification event. When published on the {@link EventBus}, a popup is shown to the user.
 */
public class Notification implements Event {

    public enum Level {
        INFO, WARNING, ERROR
    }

    private final String text;
    private final Level level;

    public Notification( Level level, String text ) {
        this.text = text;
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public Level getLevel() {
        return level;
    }
}
