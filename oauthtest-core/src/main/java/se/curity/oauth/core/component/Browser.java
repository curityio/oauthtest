package se.curity.oauth.core.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URI;

/**
 * A component that shows a browser window in it.
 */
public class Browser extends VBox
{

    @FXML
    private Text text;

    @FXML
    private WebView webView;

    private final String _message;
    private final URI _initialUri;

    public Browser(String message, URI initialUri)
    {
        _message = message;
        _initialUri = initialUri;

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

        // FIXME failing to load https. Use the configured keystore somehow
        webView.getEngine().load(_initialUri.toString());
        text.setText(_message);

        webView.getEngine().getLoadWorker().stateProperty().addListener(
                (observableValue, oldState, newState) ->
                {
                    System.out.println("WebEngine: Old state: " + oldState + ", newState: " + newState);
                    System.out.println("Observable: " + observableValue);
                }
        );
    }


}
