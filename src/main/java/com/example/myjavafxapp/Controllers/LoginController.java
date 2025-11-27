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
        acc_selector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (observable == null) {
                return;
            }
            if (newValue == null) {
                acc_selector.setValue(oldValue);
                return;
            }
            Model.getInstance().getViewFactory().setLoginAccountType(newValue);
        });
        login_btn.setOnAction(event -> {
            event.consume();
            onLogin();
        });
    }

    private void onLogin() {
        Model model = Model.getInstance();
        Stage currentStage = (Stage) login_btn.getScene().getWindow();

        model.getViewFactory().closeStage(currentStage);

        AccountType selectedType = acc_selector.getValue();
        model.getViewFactory().setLoginAccountType(selectedType);

        if (selectedType == AccountType.ADMIN) {
            model.getViewFactory().showAdminWindow();
            return;
        }

        model.getViewFactory().showClientWindow();
    }
}