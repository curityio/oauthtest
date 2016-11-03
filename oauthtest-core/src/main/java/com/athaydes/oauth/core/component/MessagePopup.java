package com.athaydes.oauth.core.component;

import com.athaydes.oauth.core.util.event.EventBus;
import com.athaydes.oauth.core.util.event.Notification;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
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
    private static final double BALOON_SPACING = 5;

    private final GridPane panels;
    private final Stage dialog;

    public MessagePopup( Stage primaryStage, EventBus eventBus ) {
        eventBus.subscribe( Notification.class, this::show );

        this.panels = new GridPane();
        //panels.setFillWidth( false );
        panels.setMaxWidth( WIDTH );
        panels.setStyle( "-fx-background-color: transparent; -fx-border-style: none;" );

        Scene dialogScene = new Scene( panels, WIDTH, HEIGHT, null );

        this.dialog = new Stage();
        dialog.setResizable( false );
        dialog.initStyle( StageStyle.TRANSPARENT );
        dialog.initOwner( primaryStage );
        dialog.setScene( dialogScene );
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
        // FIXME textPane still overflows the parent
        GridPane textPane = new GridPane();
        textPane.setStyle( "-fx-background-color: " + colorFor( notification.getLevel() ) + ";" +
                "-fx-background-radius: 6;" +
                "-fx-cursor: hand;" );
        textPane.setMaxWidth( 30 );

        // hide the text within  this block to avoid accidental use
        {
            Text text = new Text( notification.getText() );
            text.setWrappingWidth( WIDTH - 15 );
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

        adjustDialog();
        dialog.show();

        Runnable removeNotification = () -> {
            if ( panels.getChildren().contains( textPane ) ) {
                panels.getChildren().remove( textPane );
                if ( panels.getChildren().isEmpty() ) {
                    dialog.hide();
                } else {
                    adjustDialog();
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

    private void adjustDialog() {
        // couldn't figure out a way to resize the dialog that did not involve waiting for the panels to resize first
        Timeline timeline = new Timeline( new KeyFrame( Duration.millis( 50 ),
                ( e ) -> {
                    double totalHeight = panels.getChildren().stream()
                            .mapToDouble( it -> ( ( Region ) it ).getHeight() )
                            .sum();

                    double spacingToAdd = panels.getChildren().size() == 1 ? 2D :
                            panels.getChildren().size() * BALOON_SPACING + 2D;

                    System.out.println( "Total height is " + totalHeight );
                    dialog.setHeight( totalHeight + spacingToAdd );
                } ) );

        timeline.play();
    }

}
