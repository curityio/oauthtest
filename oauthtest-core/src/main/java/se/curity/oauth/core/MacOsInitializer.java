package se.curity.oauth.core;

class MacOsInitializer implements OperatingSystemInitializer
{
    @Override
    public void initialize(java.awt.Image image)
    {
        com.apple.eawt.Application.getApplication().setDockIconImage(image);
    }
}
