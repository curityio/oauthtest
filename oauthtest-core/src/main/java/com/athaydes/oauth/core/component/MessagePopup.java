package com.athaydes.oauth.core.component;

import com.athaydes.oauth.core.util.event.EventBus;
import com.athaydes.oauth.core.util.event.Notification;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * A transparent message popup that may display {@link Notification} to users in balloons.
 * <p>
 * Each different {@link com.athaydes.oauth.core.util.event.Notification.Level} may be shown with a different
 * appearance, and may be hidden automatically or require the user to close it manually.
 */
public class MessagePopup {

    private static final double WIDTH = 400;
    private static final double HEIGHT = 1200;
    private static final double BALOON_SPACING = 5;

    private final VBox panels;
    private final Stage dialog;

    public MessagePopup( Stage primaryStage, EventBus eventBus ) {
        eventBus.subscribe( Notification.class, this::show );

        this.panels = new VBox( BALOON_SPACING );
        panels.setMaxWidth( WIDTH );
        panels.setStyle( "-fx-background-color: transparent; " +
                "-fx-border-style: none;" );

        Scene dialogScene = new Scene( new Group( panels ), WIDTH, HEIGHT, Color.TRANSPARENT );

        this.dialog = new Stage( StageStyle.TRANSPARENT );
        dialog.setResizable( false );
        dialog.initOwner( primaryStage );
        dialog.setScene( dialogScene );
    }

    private void show( Notification notification ) {
        NotificationBox box = new NotificationBox( notification );
        box.setTextWidth( WIDTH );

        panels.getChildren().add( 0, box );

        // set dialog position
        {
            double xOffset = Math.max( 0, dialog.getOwner().getWidth() - ( WIDTH + 10 ) );
            double x = dialog.getOwner().getX() + dialog.getOwner().getScene().getX() + dialog.getScene().getX() + xOffset;
            double y = dialog.getOwner().getY() + dialog.getOwner().getScene().getY() + dialog.getScene().getY() + 10;
            dialog.setX( x );
            dialog.setY( y );
        }

        adjustDialog();
        dialog.show();

        Runnable removeNotification = () -> {
            panels.getChildren().remove( box );
            if ( panels.getChildren().isEmpty() ) {
                dialog.hide();
            } else {
                adjustDialog();
            }
        };

        final AtomicBoolean removing = new AtomicBoolean( false );

        Consumer<Boolean> removeNotificationSlowly = ( isClick ) -> {
            if ( removing.compareAndSet( false, true ) ) {
                FadeTransition ft = new FadeTransition( Duration.millis( isClick ? 250 : 2_500 ), box );
                ft.setToValue( 0.0 );
                ft.setOnFinished( e -> removeNotification.run() );
                ft.play();
            }
        };

        box.setOnMouseClicked( ( event -> removeNotificationSlowly.accept( true ) ) );

        if ( notification.getLevel().ordinal() <= Notification.Level.INFO.ordinal() ) {
            Timeline timeline = new Timeline( new KeyFrame( Duration.seconds( 5 ),
                    ( event ) -> removeNotificationSlowly.accept( false ) ) );

            timeline.play();
        }
    }

    private void adjustDialog() {
        Timeline timeline = new Timeline( new KeyFrame( Duration.millis( 50 ),
                ( event ) -> {
                    double totalHeight = dialog.getScene().getRoot().prefHeight( 0 );
                    dialog.setHeight( totalHeight );
                } ) );
        timeline.play();
    }

}
