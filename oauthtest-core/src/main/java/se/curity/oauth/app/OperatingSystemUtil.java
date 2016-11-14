package se.curity.oauth.app;

import javax.swing.ImageIcon;

final class OperatingSystemUtil
{
    private OperatingSystemUtil() { }

    public static void initialize() throws RuntimeException
    {
        OperatingSystemInitializer operatingSystemInitializer = (i) -> {};
        java.awt.Image image = new ImageIcon(MainApplication.class.getResource("/images/favicon-normal.png")).getImage();
        String osName = System.getProperty("os.name");

        if (osName.contains("OS X"))
        {
            try
            {
                operatingSystemInitializer = (MacOsInitializer) Class.forName(
                        "se.curity.oauth.app.MacOsInitializer").getConstructor().newInstance();
            }
            catch (ReflectiveOperationException e)
            {
                throw new RuntimeException(e);
            }
        }

        operatingSystemInitializer.initialize(image);
    }
}
