package se.curity.oauth.core.controller;

import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;
import javafx.stage.WindowEvent;
import se.curity.oauth.core.state.SslState;
import se.curity.oauth.core.util.UserPreferences;
import se.curity.oauth.core.util.event.EventBus;

public class SslSettingsController
{
    @FXML
    private ToggleGroup sslOption;

    @FXML
    private TextField trustStoreFile;

    @FXML
    private TextField trustStorePassword;

    @FXML
    private TextField keystoreFile;

    @FXML
    private TextField keystorePassword;

    private final EventBus _eventBus;
    private final UserPreferences _userPreferences;

    public SslSettingsController(EventBus eventBus, UserPreferences userPreferences)
    {
        _eventBus = eventBus;
        _userPreferences = userPreferences;
    }

    @FXML
    public void initialize()
    {
        SslState initialSslState = _userPreferences.getSslPreferences();

        trustStoreFile.setText(initialSslState.getTrustStoreFile());
        trustStorePassword.setText(initialSslState.getTrustStorePassword());
        keystoreFile.setText(initialSslState.getKeystoreFile());
        keystorePassword.setText(initialSslState.getKeystorePassword());

        _eventBus.publish(initialSslState);

        sslOption.selectedToggleProperty().addListener(observable ->
        {
            SslState.SslOption selection = SslState.SslOption.valueOf(
                    sslOption.getSelectedToggle().getUserData().toString());

            boolean disableKeystoreOptions = (selection != SslState.SslOption.USE_KEYSTORE);

            trustStoreFile.setDisable(disableKeystoreOptions);
            trustStorePassword.setDisable(disableKeystoreOptions);
            keystoreFile.setDisable(disableKeystoreOptions);
            keystorePassword.setDisable(disableKeystoreOptions);
        });

        // set this property after the listener is added above so the fields are disabled if necessary
        sslOption.getToggles().stream()
                .filter(t -> t.getUserData().equals(initialSslState.getSslOption().name()))
                .findAny()
                .ifPresent(sslOption::selectToggle);

        InvalidationListener invalidationListener = observable ->
                _eventBus.publish(getSslState());

        sslOption.selectedToggleProperty().addListener(invalidationListener);
        trustStoreFile.textProperty().addListener(invalidationListener);
        trustStorePassword.textProperty().addListener(invalidationListener);
        keystoreFile.textProperty().addListener(invalidationListener);
        keystorePassword.textProperty().addListener(invalidationListener);
    }

    private SslState getSslState()
    {
        SslState.SslOption selectedSslOption = SslState.SslOption.valueOf(
                sslOption.getSelectedToggle().getUserData().toString());

        return new SslState(selectedSslOption, trustStoreFile.getText(),
                trustStorePassword.getText(), keystoreFile.getText(), keystorePassword.getText());
    }

}
