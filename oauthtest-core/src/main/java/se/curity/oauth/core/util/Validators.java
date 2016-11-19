package se.curity.oauth.core.util;

import javafx.scene.Node;
import javafx.scene.control.TextField;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Validators
{

    private static final String ERROR_MESSAGE_KEY = "error_message";

    private static final Pattern URL_PATTERN = Pattern.compile(
            "\\(?\\b([A-z]+://|[A-z0-9]+[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]");

    // accept only numbers between 0 and 999_999_999
    private static final Pattern NATURAL_NUMBER_PATTERN = Pattern.compile(
            "0*\\d{1,9}");

    public static boolean isValidInteger(String value)
    {
        return NATURAL_NUMBER_PATTERN.matcher(value).matches();
    }

    public static boolean isValidUrl(String url)
    {
        return URL_PATTERN.matcher(url).matches();
    }

    public static boolean isNotEmpty(String text)
    {
        return !text.trim().isEmpty();
    }

    public static void makeValidatableField(TextField field,
                                            Predicate<String> isValid,
                                            String errorMessage)
    {
        if (!isValid.test(field.getText()))
        {
            field.setStyle("-fx-background-color: lightpink");
            field.getProperties().put(ERROR_MESSAGE_KEY, errorMessage);
        }

        field.textProperty().addListener(observable ->
        {
            boolean valid = isValid.test(field.getText());
            if (valid)
            {
                field.setStyle("");
                field.getProperties().remove(ERROR_MESSAGE_KEY);
            }
            else
            {
                field.setStyle("-fx-background-color: lightpink");
                field.getProperties().put(ERROR_MESSAGE_KEY, errorMessage);
            }
        });
    }

    public static List<String> validateFields(Node... nodes)
    {
        List<String> errors = new ArrayList<>(2);
        for (Node node : nodes)
        {
            @Nullable Object error = node.getProperties().get(ERROR_MESSAGE_KEY);

            if (error != null)
            {
                errors.add(error.toString());
            }
        }
        return errors;
    }

}
