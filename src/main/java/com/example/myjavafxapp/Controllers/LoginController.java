package com.example.myjavafxapp.Controllers;

import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Views.AccountTyoe;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public ChoiceBox<AccountTyoe> acc_selector;
    public TextField payee_adress_fill;
    public Label payee_adress_lbl;
    public TextField password_fill;
    public Button login_btn;
    public Label error_lbl;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(event -> onLogin());
    }
    private void onLogin() {
        Stage stage = (Stage) error_lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getView
    }
}
