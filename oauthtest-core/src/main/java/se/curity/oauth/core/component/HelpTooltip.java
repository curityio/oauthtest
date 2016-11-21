package se.curity.oauth.core.component;

import javafx.geometry.Point2D;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import se.curity.oauth.core.util.PositionHelper;

/**
 *
 */
public class HelpTooltip
{

    private final Tooltip _tooltip;
    private final WebView _webView;

    public HelpTooltip()
    {
        _tooltip = new Tooltip();
        _tooltip.setOpacity(0.8);
        _tooltip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        _webView = new WebView();
        _tooltip.setGraphic(_webView);
    }

    public void showUnder(Region node)
    {
        Point2D tooltipPosition = PositionHelper.getScreenPositionOf(node)
                .add(10, node.getHeight() + 5);

        _tooltip.show(node, tooltipPosition.getX(), tooltipPosition.getY());
    }

    public void setHtml(String html)
    {
        _webView.getEngine().loadContent(html);
    }

    public void hide()
    {
        _tooltip.hide();
    }
}
