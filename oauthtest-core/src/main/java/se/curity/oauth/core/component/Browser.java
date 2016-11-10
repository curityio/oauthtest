package se.curity.oauth.core.component;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import se.curity.oauth.core.util.event.EventBus;
import se.curity.oauth.core.util.event.Notification;

import java.io.IOException;
import java.net.URI;

/**
 * A component that shows a browser window in it.
 */
public class Browser extends BorderPane
{

    @FXML
    private Text text;

    @FXML
    private WebView webView;

    private final String _message;
    private final URI _initialUri;

    private final EventBus _eventBus;

    public Browser(String message, URI initialUri, EventBus eventBus)
    {
        _message = message;
        _initialUri = initialUri;
        _eventBus = eventBus;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("/fxml/browser.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try
        {
            fxmlLoader.load();
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize()
    {
        System.out.println("Initializing webview");

        WebEngine engine = webView.getEngine();

        engine.load(_initialUri.toString());
        text.setText(_message);

        engine.getLoadWorker().progressProperty().addListener(observable ->
        {
            System.out.println("Progress bar change: " + observable);
        });

        engine.getLoadWorker().stateProperty().addListener(
                (observableValue, oldState, newState) ->
                {
                    System.out.println("WebEngine: Old state: " + oldState + ", newState: " + newState);
                    System.out.println("Observable: " + observableValue);

                    if (newState == Worker.State.FAILED)
                    {
                        Throwable error = engine.getLoadWorker().getException();

                        String errorMessage = "Failed to load page in the browser: " + error.getMessage();

                        _eventBus.publish(new Notification(Notification.Level.ERROR, errorMessage));

                        engine.getLoadWorker().getException().printStackTrace();
                    }
                }
        );
    }


}
