package se.curity.oauth.core.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import se.curity.oauth.core.state.GeneralState;
import se.curity.oauth.core.util.PreferencesUtils;
import se.curity.oauth.core.util.event.EventBus;

public class GeneralSettingsController
{
    @FXML
    private CheckBox verbose;

    private final EventBus _eventBus;
    private final PreferencesUtils _preferencesUtils;

    public GeneralSettingsController(EventBus eventBus, Stage primaryStage, PreferencesUtils preferencesUtils)
    {
        _eventBus = eventBus;
        _preferencesUtils = preferencesUtils;

        primaryStage.setOnCloseRequest(e ->
        {
            _preferencesUtils.putGeneralSettings(getGeneralState());
        });
    }

    @FXML
    public void initialize()
    {
        GeneralState initialGeneralState = _preferencesUtils.getGeneralPreferences();

        verbose.setSelected(initialGeneralState.isVerbose());

        _eventBus.publish(initialGeneralState);

        verbose.selectedProperty().addListener(observable ->  _eventBus.publish(getGeneralState()));
    }

    private GeneralState getGeneralState()
    {
        return new GeneralState(verbose.isSelected());
    }
}
