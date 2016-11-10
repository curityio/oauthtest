package se.curity.oauth.core.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import se.curity.oauth.core.state.GeneralState;
import se.curity.oauth.core.util.UserPreferences;
import se.curity.oauth.core.util.event.EventBus;

public class GeneralSettingsController
{
    @FXML
    private CheckBox verbose;

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

        _eventBus.publish(initialGeneralState);

        verbose.selectedProperty().addListener(observable -> _eventBus.publish(getGeneralState()));
    }

    private GeneralState getGeneralState()
    {
        return new GeneralState(verbose.isSelected());
    }
}
