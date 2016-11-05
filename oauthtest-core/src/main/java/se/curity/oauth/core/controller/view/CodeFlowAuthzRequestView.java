package se.curity.oauth.core.controller.view;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import se.curity.oauth.core.controller.CodeFlowController;
import se.curity.oauth.core.state.CodeFlowAuthzState;

/**
 * Code flow authorization request view.
 */
public class CodeFlowAuthzRequestView implements CodeFlowController.CodeFlowRequestView {
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
