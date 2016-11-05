package se.curity.oauth.core.controller.view;

import se.curity.oauth.core.state.CodeFlowAuthzState;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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

    public void setInvalidationListener( InvalidationListener authzFieldChangeListener ) {
        responseTypeField.textProperty().addListener( authzFieldChangeListener );
        clientIdField.textProperty().addListener( authzFieldChangeListener );
        redirectUriField.textProperty().addListener( authzFieldChangeListener );
        scopeField.textProperty().addListener( authzFieldChangeListener );
        stateField.textProperty().addListener( authzFieldChangeListener );
    }

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
