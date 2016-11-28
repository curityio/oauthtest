package se.curity.oauth.core.component;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
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

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

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

    private final EventBus _eventBus;
    private final ObservableCookieManager _cookieManager;

    @Nullable
    private InvalidationListener _tempLocationListener = null;

    @Nullable
    private ChangeListener<Worker.State> _tempStateChangeListener = null;

    public Browser(EventBus eventBus,
                   ObservableCookieManager cookieManager)
    {
        _eventBus = eventBus;
        _cookieManager = cookieManager;

        cookieManager.addCookieListener(cookie ->
        {
            // TODO later we should make the cookies visible in the UI
            System.out.printf("ADDED COOKIE (%s): %s%n", cookie.getPath(), cookie);
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
        WebEngine engine = webView.getEngine();
        progressBar.progressProperty().bind(engine.getLoadWorker().progressProperty());
    }

    public void initializeWith(String message, URI initialUri, Consumer<URI> onLoadPage)
    {
        System.out.println("Initializing webview");

        WebEngine engine = webView.getEngine();

        cleanupListeners();

        _tempLocationListener = observable ->
        {
            String uriString = ((ReadOnlyStringProperty) observable).getValueSafe();
            System.out.println("Browser loading URL: " + uriString);

            Platform.runLater(() ->
            {
                onLoadPage.accept(URI.create(uriString));
                urlText.setText(uriString);
            });
        };

        _tempStateChangeListener = (observableValue, oldState, newState) ->
        {
            System.out.printf("WebEngine: Old state: %s, newState: %s%n", oldState, newState);

            if (newState == Worker.State.FAILED)
            {
                Throwable error = engine.getLoadWorker().getException();

                String errorMessage = "Failed to load page in the browser: " + error.getMessage();

                _eventBus.publish(new Notification(Notification.Level.ERROR, errorMessage));

                error.printStackTrace();
            }
        };

        engine.getLoadWorker().stateProperty().addListener(_tempStateChangeListener);
        engine.locationProperty().addListener(_tempLocationListener);

        engine.load(initialUri.toString());
        text.setText(message);
    }

    private void cleanupListeners()
    {
        WebEngine engine = webView.getEngine();

        if (_tempLocationListener != null)
        {
            engine.locationProperty().removeListener(_tempLocationListener);
        }

        if (_tempStateChangeListener != null)
        {
            engine.getLoadWorker().stateProperty().removeListener(_tempStateChangeListener);
        }
    }

    @FXML
    protected void close()
    {
        getScene().getWindow().hide();
        cleanupListeners();
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

    public List<HttpCookie> getCookies()
    {
        return _cookieManager.getCookieStore().getCookies();
    }

    public void removeAllCookies()
    {
        _cookieManager.getCookieStore().removeAll();
    }

}
