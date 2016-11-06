package se.curity.oauth.core.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import se.curity.oauth.core.util.event.Notification;

import java.io.IOException;

/**
 * A notification box that is displayed to the user as a text balloon.
 */
public class NotificationBox extends GridPane
{

    @FXML
    private Text text;

    public NotificationBox(Notification notification)
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("/fxml/notification-box.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try
        {
            fxmlLoader.load();
            setStyle("-fx-background-radius: 4; -fx-background-color: " +
                    colorFor(notification.getLevel()));
            text.setText(notification.getText());
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public void setTextWidth(double width)
    {
        setMinWidth(width);
        text.setWrappingWidth(width);
        setMinHeight(text.maxHeight(width));
    }

    private static String colorFor(Notification.Level level)
    {
        switch (level)
        {
            case ERROR:
                return "lightsalmon";
            case WARNING:
                return "lightpink";
            default:
                return "antiquewhite";
        }
    }

}
