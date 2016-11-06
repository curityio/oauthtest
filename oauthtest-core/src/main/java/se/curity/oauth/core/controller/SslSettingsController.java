package se.curity.oauth.core.controller;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import se.curity.oauth.core.state.SslState;
import se.curity.oauth.core.util.PreferencesUtils;
import se.curity.oauth.core.util.event.EventBus;

public class SslSettingsController
{
    @FXML
    private CheckBox ignoreSSL;

    @FXML
    private TextField trustStoreFile;

    @FXML
    private TextField trustStorePassword;

    @FXML
    private TextField keystoreFile;

    @FXML
    private TextField keystorePassword;

    private final EventBus _eventBus;
    private final PreferencesUtils _preferencesUtils;

    public SslSettingsController(EventBus eventBus, PreferencesUtils preferencesUtils)
    {
        _eventBus = eventBus;
        _preferencesUtils = preferencesUtils;
    }

    @FXML
    public void initialize()
    {
        SslState initialSslState = _preferencesUtils.getSslPreferences();

        trustStoreFile.setText(initialSslState.getTrustStoreFile());
        trustStorePassword.setText(initialSslState.getTrustStorePassword());
        keystoreFile.setText(initialSslState.getKeystoreFile());
        keystorePassword.setText(initialSslState.getKeystorePassword());

        _eventBus.publish(initialSslState);

        ignoreSSL.selectedProperty().addListener(observable ->
        {
            trustStoreFile.setDisable(ignoreSSL.isSelected());
            trustStorePassword.setDisable(ignoreSSL.isSelected());
            keystoreFile.setDisable(ignoreSSL.isSelected());
            keystorePassword.setDisable(ignoreSSL.isSelected());
        });

        // set this property after the listener is added above so the fields are disabled if necessary
        ignoreSSL.setSelected(initialSslState.isIgnoreSSL());

        InvalidationListener invalidationListener = observable ->
                _eventBus.publish(getSslState());

        ignoreSSL.selectedProperty().addListener(invalidationListener);
    }

    private SslState getSslState()
    {
        return new SslState(ignoreSSL.isSelected(), trustStoreFile.getText(),
                trustStorePassword.getText(), keystoreFile.getText(), keystorePassword.getText());
    }

}
