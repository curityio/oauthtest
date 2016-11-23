package se.curity.oauth.core.controller.flows.code;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import se.curity.oauth.core.component.HelpTooltip;
import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.util.Validators;
import se.curity.oauth.core.util.Workers;

import java.util.List;
import java.util.Properties;

/**
 * Code flow authorization request view.
 */
public class CodeFlowAuthzRequestController implements CodeFlowController.CodeFlowRequestController
{
    @FXML
    private TextField responseTypeField;
    @FXML
    private TextField clientIdField;
    @FXML
    private TextField redirectUriField;
    @FXML
    private TextField scopeField;
    @FXML
    private TextField stateField;
    @FXML
    private ImageView scopeImageView;
    @FXML
    public ImageView redirectUriImageView;

    private final Workers _workers;
    private final HelpTooltip _helpTooltip;

    public CodeFlowAuthzRequestController(Workers workers,
                                          HelpTooltip helpTooltip)
    {
        _workers = workers;
        _helpTooltip = helpTooltip;
    }

    @FXML
    private void initialize()
    {
        _workers.runInBackground(() ->
        {
            Properties codeFlowViewProperties = new Properties();
            codeFlowViewProperties.load(getClass().getResourceAsStream(
                    "/properties/flows/code/authorize_request.properties"));
            return codeFlowViewProperties;
        }).onSuccess(properties ->
        {
            String scopeText = properties.getProperty("scope");
            String redirectUriText = properties.getProperty("redirect_uri");

            Platform.runLater(() ->
            {
                scopeImageView.setOnMouseEntered(event ->
                {
                    _helpTooltip.setHtml(scopeText);
                    _helpTooltip.showUnder(scopeField);
                });
                scopeImageView.setOnMouseExited(event -> _helpTooltip.hide());

                redirectUriImageView.setOnMouseEntered(event ->
                {
                    _helpTooltip.setHtml(redirectUriText);
                    _helpTooltip.showUnder(redirectUriField);
                });
                redirectUriImageView.setOnMouseExited(event -> _helpTooltip.hide());
            });
        });
    }

    @Override
    public void setInvalidationListener(InvalidationListener authzFieldChangeListener)
    {
        Validators.makeValidatableField(clientIdField,
                Validators::isNotEmpty, "client_id cannot be empty");

        responseTypeField.textProperty().addListener(authzFieldChangeListener);
        clientIdField.textProperty().addListener(authzFieldChangeListener);
        redirectUriField.textProperty().addListener(authzFieldChangeListener);
        scopeField.textProperty().addListener(authzFieldChangeListener);
        stateField.textProperty().addListener(authzFieldChangeListener);
    }

    public CodeFlowAuthzState getCodeFlowAuthzState()
    {
        List<String> errors = Validators.validateFields(clientIdField);
        if (errors.isEmpty())
        {
            return CodeFlowAuthzState.validState(
                    responseTypeField.getText(),
                    clientIdField.getText(),
                    redirectUriField.getText(),
                    scopeField.getText(),
                    stateField.getText());
        }
        else
        {
            return CodeFlowAuthzState.invalid(errors);
        }
    }

}
