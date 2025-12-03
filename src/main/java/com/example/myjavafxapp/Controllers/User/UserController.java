package com.example.myjavafxapp.Controllers.User;

import com.example.myjavafxapp.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    public BorderPane user_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case TRANSACTIONS -> user_parent.setCenter(Model.getInstance().getViewFactory().getTransactionsView());
                case ACCOUNTS -> user_parent.setCenter(Model.getInstance().getViewFactory().getAccountsView());
                default -> user_parent.setCenter(Model.getInstance().getViewFactory().getDashboardView());
            }
        });
    }
}
