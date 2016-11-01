package com.athaydes.oauth.core.component;

import com.athaydes.oauth.core.util.event.EventBus;
import com.athaydes.oauth.core.util.event.Notification;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * A transparent message popup that may display {@link Notification} to users in balloons.
 * <p>
 * Each different {@link com.athaydes.oauth.core.util.event.Notification.Level} may be shown with a different
 * appearance, and may be hidden automatically or require the user to close it manually.
 */
public class MessagePopup {

    private static final double WIDTH = 400;
    private static final double HEIGHT = 900;

    private final VBox panels;
    private final Stage dialog;

    public MessagePopup( Stage primaryStage, EventBus eventBus ) {
        eventBus.subscribe( Notification.class, this::show );

        this.panels = new VBox( 5 );
        panels.setStyle( "-fx-background-color: transparent; -fx-border-style: none;" );
        Scene dialogScene = new Scene( panels, WIDTH, HEIGHT, null );

        this.dialog = new Stage();
        dialog.setResizable( false );
        dialog.initStyle( StageStyle.TRANSPARENT );
        dialog.initOwner( primaryStage );
        dialog.setScene( dialogScene );

        // FIXME not working
        dialog.maxHeightProperty().bind( panels.heightProperty().add( 30 ) );
    }

    private static String colorFor( Notification.Level level ) {
        switch ( level ) {
            case ERROR:
                return "lightsalmon";
            case WARNING:
                return "lightpink";
            default:
                return "antiquewhite";
        }
    }

    private void show( Notification notification ) {
        GridPane textPane = new GridPane();
        textPane.setStyle( "-fx-background-color: " + colorFor( notification.getLevel() ) + ";" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" );

        // hide the text within  this block to avoid accidental use
        {
            Text text = new Text( notification.getText() );
            textPane.getChildren().add( text );
            GridPane.setMargin( text, new Insets( 10 ) );
        }

        panels.getChildren().add( 0, textPane );

        // set dialog position
        {
            double xOffset = Math.max( 0, dialog.getOwner().getWidth() - ( WIDTH + 10 ) );
            double x = dialog.getOwner().getX() + dialog.getOwner().getScene().getX() + dialog.getScene().getX() + xOffset;
            double y = dialog.getOwner().getY() + dialog.getOwner().getScene().getY() + dialog.getScene().getY() + 10;
            dialog.setX( x );
            dialog.setY( y );
        }

        dialog.show();

        Runnable removeNotification = () -> {
            if ( panels.getChildren().contains( textPane ) ) {
                panels.getChildren().remove( textPane );
                if ( panels.getChildren().isEmpty() ) {
                    dialog.hide();
                }
            }
        };

        textPane.setOnMouseClicked( ( event -> removeNotification.run() ) );

        if ( notification.getLevel().ordinal() <= Notification.Level.INFO.ordinal() ) {
            Timeline timeline = new Timeline( new KeyFrame( Duration.seconds( 5 ),
                    ( event ) -> removeNotification.run() ) );

            timeline.play();
        }
    }

}
