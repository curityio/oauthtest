package se.curity.oauth.core.controller;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import se.curity.oauth.core.state.GeneralState;
import se.curity.oauth.core.util.UserPreferences;
import se.curity.oauth.core.util.Validators;
import se.curity.oauth.core.util.event.EventBus;

public class GeneralSettingsController
{
    @FXML
    private CheckBox verbose;
    @FXML
    private TextField maxNotificationRows;

    private final EventBus _eventBus;
    private final UserPreferences _userPreferences;

    public GeneralSettingsController(EventBus eventBus, Stage primaryStage, UserPreferences userPreferences)
    {
        _eventBus = eventBus;
        _userPreferences = userPreferences;

        primaryStage.setOnCloseRequest(e ->
        {
            _userPreferences.putGeneralSettings(getGeneralState());
        });
    }

    @FXML
    protected void initialize()
    {
        GeneralState initialGeneralState = _userPreferences.getGeneralPreferences();

        verbose.setSelected(initialGeneralState.isVerbose());
        maxNotificationRows.setText(Integer.toString(initialGeneralState.getMaximumNotificationRows()));

        _eventBus.publish(initialGeneralState);

        InvalidationListener invalidationListener = observable -> _eventBus.publish(getGeneralState());

        verbose.selectedProperty().addListener(invalidationListener);
        maxNotificationRows.textProperty().addListener(invalidationListener);

        Validators.makeValidatableField(maxNotificationRows, Validators::isValidInteger,
                "'maxNotificationRows' must be an integer");
    }

    private GeneralState getGeneralState()
    {
        return new GeneralState(verbose.isSelected(),
                integerOrDefault(maxNotificationRows.getText(), GeneralState.DEFAULT_MAX_NOTIFICATION_ROWS));
    }

    private static int integerOrDefault(String textValue, int defaultValue)
    {
        if (Validators.isValidInteger(textValue))
        {
            return Integer.valueOf(textValue);
        }
        else
        {
            return defaultValue;
        }
    }

}
