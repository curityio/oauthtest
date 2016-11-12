package se.curity.oauth.core.component;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import se.curity.oauth.core.util.ObservableCookieManager;
import se.curity.oauth.core.util.event.EventBus;
import se.curity.oauth.core.util.event.Notification;

import java.io.IOException;
import java.net.URI;
import java.util.function.BiConsumer;

/**
 * A component that shows a browser window in it.
 */
public class Browser extends BorderPane
{

    @FXML
    private Label urlText;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button backButton;

    @FXML
    private Button nextButton;

    @FXML
    private Text text;

    @FXML
    private WebView webView;

    private final String _message;
    private final URI _initialUri;
    private final EventBus _eventBus;
    private final BiConsumer<Browser, URI> _onLoadPage;

    /**
     * This factory allows a Browser instance to be created by Dependency Injection more easily because it does not
     * require the user of {@link Browser} to provide all its dependencies to create one,
     * only the runtime dependencies which cannot be injected.
     */
    public static class Factory
    {
        private final EventBus _eventBus;
        private final ObservableCookieManager _cookieManager;

        public Factory(EventBus eventBus, ObservableCookieManager cookieManager)
        {
            _eventBus = eventBus;
            _cookieManager = cookieManager;
        }

        public Browser create(String message, URI initialUri, BiConsumer<Browser, URI> onLoadPage)
        {
            return new Browser(message, initialUri, _eventBus, _cookieManager, onLoadPage);
        }
    }

    private Browser(String message, URI initialUri,
                    EventBus eventBus,
                    ObservableCookieManager cookieManager,
                    BiConsumer<Browser, URI> onLoadPage)
    {
        _message = message;
        _initialUri = initialUri;
        _eventBus = eventBus;
        _onLoadPage = onLoadPage;

        cookieManager.addCookieListener(cookie ->
        {
            System.out.println("ADDED COOKIE: " + cookie);
        });

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

        progressBar.progressProperty().bind(engine.getLoadWorker().progressProperty());

        engine.locationProperty().addListener(observable ->
        {
            String uriString = ((ReadOnlyStringProperty) observable).getValueSafe();

            _onLoadPage.accept(this, URI.create(uriString));

            System.out.println("Looks like we got to URL : " + uriString);
            urlText.setText(uriString);
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

                        error.printStackTrace();
                    }
                }
        );
    }

    @FXML
    protected void close()
    {
        getScene().getWindow().hide();
    }

    @FXML
    protected void previous()
    {
        webView.getEngine().executeScript("history.back()");
    }

    @FXML
    protected void next()
    {
        webView.getEngine().executeScript("history.forward()");
    }

    public Button getBackButton()
    {
        return backButton;
    }

    public Button getNextButton()
    {
        return nextButton;
    }

    public WebEngine getWebEngine()
    {
        return webView.getEngine();
    }

}
