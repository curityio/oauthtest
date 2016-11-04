package io.curity.oauth.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.BuilderFactory;

/**
 * The OAuth-Test JavaFX Application.
 */
public class OAuthTestApplication extends Application {

    public static void main( String[] args ) {
        launch( args );
    }

    public void start( Stage primaryStage ) throws Exception {
        primaryStage.setTitle( "OAuthTest" );

        BuilderFactory builderFactory = new JavaFXBuilderFactory();

        Parent oauthFlows = FXMLLoader.load(
                getClass().getResource( "/fxml/main.fxml" ),
                null,
                builderFactory,
                new ApplicationContainer( primaryStage )::get );

        primaryStage.setScene( new Scene( oauthFlows, 600, 600 ) );
        primaryStage.show();
    }
}
