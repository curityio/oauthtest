package se.curity.oauth.core.controller.flows.code;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import se.curity.oauth.core.state.CodeFlowAuthzState;
import se.curity.oauth.core.util.Validators;

import java.util.List;

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
