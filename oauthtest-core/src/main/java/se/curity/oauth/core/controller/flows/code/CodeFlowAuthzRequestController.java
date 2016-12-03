package se.curity.oauth.core.controller.flows.code;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.curity.oauth.core.component.HelpTooltip;
import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.util.UserPreferences;
import se.curity.oauth.core.util.Validators;
import se.curity.oauth.core.util.Workers;
import se.curity.oauth.core.util.event.EventBus;

import java.util.List;
import java.util.Objects;
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

    private final EventBus _eventBus;
    private final UserPreferences _userPreferences;
    private final Workers _workers;
    private final HelpTooltip _helpTooltip;

    public CodeFlowAuthzRequestController(EventBus eventBus, Stage primaryStage, UserPreferences userPreferences,
                                          Workers workers, HelpTooltip helpTooltip)
    {
        _workers = workers;
        _helpTooltip = helpTooltip;
        _userPreferences = userPreferences;
        _eventBus = eventBus;

        EventHandler<WindowEvent> onCloseRequest = primaryStage.getOnCloseRequest();

        primaryStage.setOnCloseRequest(e ->
        {
            _userPreferences.putCodeFlowPreferences(getCodeFlowAuthzState());

            if (onCloseRequest != null)
            {
                onCloseRequest.handle(e);
            }
        });
    }

    @FXML
    private void initialize()
    {
        CodeFlowAuthzState initialCodeFlowPreferences = _userPreferences.getCodeFlowPreferences();

        String responseType = initialCodeFlowPreferences.getResponseType();

        if (!Objects.equals(responseType, "code"))
        {
            System.err.println(String.format("Cannot initialize code flow with any other response type " +
                    "then 'code'. The value '%s' is not valid", responseType));

            responseType = "code";
        }

        responseTypeField.setText(responseType);
        clientIdField.setText(initialCodeFlowPreferences.getClientId());
        redirectUriField.setText(initialCodeFlowPreferences.getRedirectUri());
        scopeField.setText(initialCodeFlowPreferences.getScope());
        stateField.setText(initialCodeFlowPreferences.getState());

        _eventBus.publish(initialCodeFlowPreferences);

        setInvalidationListener(observable -> _eventBus.publish(getCodeFlowAuthzState()));

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
