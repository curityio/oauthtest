package se.curity.oauth.core.state;

import se.curity.oauth.core.util.event.Event;

public class GeneralState implements Event
{
    private final boolean _verbose;

    public GeneralState(boolean verbose)
    {
        _verbose = verbose;
    }

    public boolean isVerbose()
    {
        return _verbose;
    }
}
