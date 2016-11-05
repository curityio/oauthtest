package se.curity.oauth.core.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class SettingsController
{
    @FXML
    private TextField trustStoreFile;

    @FXML
    private TextField trustStorePassword;

    @FXML
    private TextField keystoreFile;

    @FXML
    private TextField keystorePassword;
}
