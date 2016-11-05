package se.curity.oauth.core.util;

import javafx.scene.control.TextField;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Validators {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "\\(?\\b([A-z]+://|[A-z0-9]+[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]" );

    public static boolean isValidUrl( String url ) {
        return URL_PATTERN.matcher( url ).matches();
    }

    public static void makeValidatableField( TextField field, Predicate<String> isValid ) {
        field.textProperty().addListener( observable -> {
            boolean valid = isValid.test( field.getText() );
            if ( valid ) {
                field.setStyle( "" );
            } else {
                field.setStyle( "-fx-background-color: lightpink" );
            }
        } );
    }

}
