package se.curity.oauth.core;

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
                        "se.curity.oauth.core.MacOsInitializer").getDeclaredConstructors()[0].newInstance();
            }
            catch (ReflectiveOperationException e)
            {
                throw new RuntimeException(e);
            }
        }

        operatingSystemInitializer.initialize(image);
    }
}
