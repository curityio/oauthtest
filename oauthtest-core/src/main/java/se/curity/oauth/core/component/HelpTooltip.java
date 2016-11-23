package se.curity.oauth.core.component;

import javafx.animation.FadeTransition;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import se.curity.oauth.core.util.PositionHelper;

/**
 * A powerful tooltip popup which can display HTML text (via {@link WebView}) near a given Node.
 */
public class HelpTooltip
{

    private final Stage _dialog;
    private final WebView _webView;
    private final Node _root;
    private final FadeTransition _hideAnimation;

    // remember latest HTML loaded so we can go back to it
    private String _html = "";

    public HelpTooltip(Stage primaryStage)
    {
        StackPane root = new StackPane();
        root.setBackground(Background.EMPTY);

        _root = root;

        _webView = new WebView();
        _webView.setPrefSize(500, 200);

        Scene dialogScene = new Scene(root, 500, 200, Color.TRANSPARENT);

        root.getChildren().add(_webView);

        this._dialog = new Stage(StageStyle.TRANSPARENT);
        _dialog.initOwner(primaryStage);
        _dialog.setScene(dialogScene);
        _dialog.setResizable(true);
        _dialog.setOpacity(0.8);
        _dialog.hide();

        _hideAnimation = new FadeTransition(Duration.millis(1_500L), root);
        _hideAnimation.setFromValue(1.0);
        _hideAnimation.setToValue(0.0);
        _hideAnimation.setOnFinished(e -> _dialog.hide());

        _root.setOnMouseEntered(e -> stopHiding());
        _root.setOnMouseExited(e -> hide());

        FlowPane buttons = new FlowPane(3.0, 3.0);
        buttons.setMaxSize(200, 20);

        // TODO use graphical buttons instead
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> goBack());
        Button forwardButton = new Button("Forward");
        forwardButton.setOnAction(e -> goForward());
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> _dialog.hide());

        buttons.getChildren().addAll(
                backButton, forwardButton, closeButton);

        root.getChildren().add(buttons);
        StackPane.setAlignment(buttons, Pos.BOTTOM_RIGHT);
    }

    public void showUnder(Region node)
    {
        Point2D tooltipPosition = PositionHelper.getScreenPositionOf(node)
                .add(10, node.getHeight() + 5);

        stopHiding();

        _dialog.getScene().getWindow().setX(tooltipPosition.getX());
        _dialog.getScene().getWindow().setY(tooltipPosition.getY());
        _dialog.show();
    }

    public void setHtml(String html)
    {
        _html = html;
        _webView.getEngine().loadContent(html);
    }

    public void hide()
    {
        if (_dialog.isShowing())
        {
            _hideAnimation.play();
        }
    }

    private void stopHiding()
    {
        _hideAnimation.stop();
        _root.setOpacity(1.0);
    }

    private void goBack()
    {
        WebHistory history = _webView.getEngine().getHistory();
        int currentIndex = history.getCurrentIndex();
        if (currentIndex > 0)
        {
            history.go(-1);
        }
        else if (!_html.isEmpty())
        {
            // history does not remember the initially loaded HTML, unfortunately
            _webView.getEngine().loadContent(_html);
        }
    }

    private void goForward()
    {
        WebHistory history = _webView.getEngine().getHistory();

        if (history.getCurrentIndex() < history.getEntries().size() - 1)
        {
            _webView.getEngine().getHistory().go(1);
        }
    }

}
