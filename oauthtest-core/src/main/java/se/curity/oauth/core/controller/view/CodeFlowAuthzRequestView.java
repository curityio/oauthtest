package se.curity.oauth.core.controller.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import se.curity.oauth.core.state.CodeFlowAuthzState;

/**
 * Code flow authorization request view.
 */
public class CodeFlowAuthzRequestView {
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

    public CodeFlowAuthzState getCodeFlowAuthzState() {
        return new CodeFlowAuthzState(
                responseTypeField.getText(),
                clientIdField.getText(),
                redirectUriField.getText(),
                scopeField.getText(),
                stateField.getText()
        );
    }

}
