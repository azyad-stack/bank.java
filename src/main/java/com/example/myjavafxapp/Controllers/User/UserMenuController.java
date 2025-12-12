package com.example.myjavafxapp.Controllers.User;

import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Views.UserMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class UserMenuController implements Initializable {
    public Button dashboard_btn;
    public Button transactions_btn;
    public Button accounts_btn;
    public Button profile_btn;
    public Button logout_btn;
    public Button report_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addListeners();
    }

    private void addListeners() {
        dashboard_btn.setOnAction(event -> onDashboard());
        transactions_btn.setOnAction(event -> onTransactions());
        accounts_btn.setOnAction(event -> onAccounts());
        logout_btn.setOnAction(event -> onLogout());
    }

    private void onLogout() {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(UserMenuOptions.LOGOUT);
    }

    private void onAccounts() {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(UserMenuOptions.ACCOUNTS);
    }

    private void onTransactions() {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(UserMenuOptions.TRANSACTIONS);
    }

    private void onDashboard() {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(UserMenuOptions.DASHBOARD);

    }
}
