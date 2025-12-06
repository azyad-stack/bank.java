package com.example.myjavafxapp.Controllers; // Ensure this matches your directory structure

import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Views.AccountType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    public ChoiceBox<AccountType> acc_selector; // Added <String> generic for safety
    @FXML
    public TextField payee_adress_fill;
    @FXML
    public Label payee_adress_lbl;
    @FXML
    public TextField password_fill;
    @FXML
    public Button login_btn;
    @FXML
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        acc_selector.setItems(FXCollections.observableArrayList(AccountType.CLIENT, AccountType.ADMIN));
        acc_selector.setValue(Model.getInstance().getViewFactory().getLoginAccountType());
        acc_selector.valueProperty().addListener(observable -> Model.getInstance().getViewFactory().setLoginAccountType(acc_selector.getValue()));
        login_btn.setOnAction(event -> onLogin());
    }

    private void onLogin() {
        // Clear any previous error
        error_lbl.setText("");

        // Validate inputs first
        if (payee_adress_fill.getText().trim().isEmpty() || password_fill.getText().trim().isEmpty()) {
            error_lbl.setText("Please enter both username and password!");
            return;  // Don't close window, just return
        }

        if (Model.getInstance().getViewFactory().getLoginAccountType() == AccountType.CLIENT) {
            // Evaluate Client Login Credentials
            Model.getInstance().evaluateClientCred(payee_adress_fill.getText(), password_fill.getText());

            if (Model.getInstance().getClientLoginSuccessFlag()) {
                // ✅ SUCCESS - Now close the window and open client window
                Stage stage = (Stage) error_lbl.getScene().getWindow();
                Model.getInstance().getViewFactory().showClientWindow();
                Model.getInstance().getViewFactory().closeStage(stage);
            } else {
                // ❌ FAILED - Keep window open and show error
                payee_adress_fill.setText("");
                password_fill.setText("");
                error_lbl.setText("Error! Incorrect username or password!");
            }
        } else {
            // ADMIN LOGIN - you can add authentication here later

            Stage stage = (Stage) error_lbl.getScene().getWindow();
            Model.getInstance().getViewFactory().showAdminWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
        }
    }
}