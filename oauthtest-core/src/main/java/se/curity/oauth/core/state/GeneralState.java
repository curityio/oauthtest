package se.curity.oauth.core.state;

import se.curity.oauth.core.util.event.Event;

public class GeneralState implements Event
{
    public static final boolean DEFAULT_VERBOSE = true;
    public static final int DEFAULT_MAX_NOTIFICATION_ROWS = 50;

    private final boolean _verbose;
    private final int _maximumNotificationRows;

    public GeneralState(boolean verbose, int maximumNotificationRows)
    {
        _verbose = verbose;
        _maximumNotificationRows = maximumNotificationRows;
    }

    public boolean isVerbose()
    {
        return _verbose;
    }

    public int getMaximumNotificationRows()
    {
        return _maximumNotificationRows;
    }

}
