package se.curity.oauth.core.util;

import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * Utility functions to handle the position of Nodes.
 */
public class PositionHelper
{

    public static Point2D getScreenPositionOf(Node node)
    {
        Point2D p = node.localToScene(Point2D.ZERO);
        return new Point2D(p.getX() + node.getScene().getX() + node.getScene().getWindow().getX(),
                p.getY() + node.getScene().getY() + node.getScene().getWindow().getY());
    }
}
